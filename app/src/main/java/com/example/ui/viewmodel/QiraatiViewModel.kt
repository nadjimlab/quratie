package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiStudyService
import com.example.ai.ParsedSlotDraft
import com.example.algeria.AlgerianCurriculum
import com.example.data.database.QiraatiDatabase
import com.example.data.model.*
import com.example.data.repository.QiraatiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class AppDestination(val route: String) {
    data object Home : AppDestination("home")
    data object Tomorrow : AppDestination("tomorrow")
    data object Timetable : AppDestination("timetable")
    data object HomeworkAndExams : AppDestination("homework_exams")
    data object AssistantAndMore : AppDestination("more")
}

class QiraatiViewModel(application: Application) : AndroidViewModel(application) {

    private val database = QiraatiDatabase.getDatabase(application, viewModelScope)
    val repository = QiraatiRepository(database)
    private val aiService = GeminiStudyService()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            com.example.data.database.populateInitialData(database)
        }
    }

    val allStudents = repository.allStudents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _activeStudentId = MutableStateFlow<Long?>(null)
    val activeStudentId = _activeStudentId.asStateFlow()

    private val _selectedDayIndex = MutableStateFlow(
        Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    )
    val selectedDayIndex = _selectedDayIndex.asStateFlow()

    val allSubjects = repository.allSubjects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current active student entity
    val activeStudent: StateFlow<StudentEntity?> = combine(
        allStudents,
        _activeStudentId
    ) { students, id ->
        if (id != null) {
            students.find { it.id == id } ?: students.firstOrNull()
        } else {
            students.find { it.isDefault } ?: students.firstOrNull()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Timetable for selected day
    val selectedDaySlots: StateFlow<List<TimetableSlotWithSubject>> = combine(
        activeStudent,
        _selectedDayIndex
    ) { student, dayIndex ->
        if (student != null) {
            repository.getSlotsForDay(student.id, dayIndex)
        } else {
            flowOf(emptyList())
        }
    }.flatMapLatest { it }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Today's Slots
    val todaySlots: StateFlow<List<TimetableSlotWithSubject>> = activeStudent.flatMapLatest { student ->
        if (student != null) {
            val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            repository.getSlotsForDay(student.id, today)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Tomorrow preparation summary
    val tomorrowSummary: StateFlow<TomorrowPreparationSummary?> = activeStudent.flatMapLatest { student ->
        if (student != null) {
            repository.getTomorrowPreparationSummary(student.id)
        } else {
            flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Homework for active student
    val homeworkList: StateFlow<List<HomeworkWithSubject>> = activeStudent.flatMapLatest { student ->
        if (student != null) {
            repository.getHomeworkForStudent(student.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Exams for active student
    val examsList: StateFlow<List<ExamWithSubject>> = activeStudent.flatMapLatest { student ->
        if (student != null) {
            repository.getExamsForStudent(student.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Backpack items
    val backpackItems: StateFlow<List<BackpackItemEntity>> = activeStudent.flatMapLatest { student ->
        if (student != null) {
            repository.getBackpackItems(student.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Notifications
    val notifications: StateFlow<List<AppNotificationEntity>> = activeStudent.flatMapLatest { student ->
        if (student != null) {
            repository.getNotifications(student.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // AI Chat Messages
    private val _aiChatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "AI",
                text = "مرحباً بك! أنا مساعدك الدراسي الذكي في «قِرايتي». كيف يمكنني مساعدتك اليوم في تنظيم دراسة أبنائك؟"
            )
        )
    )
    val aiChatMessages = _aiChatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading = _isAiLoading.asStateFlow()

    // OCR / Schedule Scanner state
    private val _ocrDraftSlots = MutableStateFlow<List<ParsedSlotDraft>>(emptyList())
    val ocrDraftSlots = _ocrDraftSlots.asStateFlow()

    private val _isOcrScanning = MutableStateFlow(false)
    val isOcrScanning = _isOcrScanning.asStateFlow()

    // Preferences & UI State
    private val _appLanguage = MutableStateFlow("AR") // "AR" or "FR"
    val appLanguage = _appLanguage.asStateFlow()

    private val _showCelebration = MutableStateFlow(false)
    val showCelebration = _showCelebration.asStateFlow()

    private val _showProDialog = MutableStateFlow(false)
    val showProDialog = _showProDialog.asStateFlow()

    fun selectStudent(studentId: Long) {
        viewModelScope.launch {
            _activeStudentId.value = studentId
            repository.selectStudent(studentId)
        }
    }

    fun setSelectedDay(dayIndex: Int) {
        _selectedDayIndex.value = dayIndex
    }

    fun toggleLanguage() {
        _appLanguage.value = if (_appLanguage.value == "AR") "FR" else "AR"
    }

    fun openProDialog() {
        _showProDialog.value = true
    }

    fun closeProDialog() {
        _showProDialog.value = false
    }

    fun dismissCelebration() {
        _showCelebration.value = false
    }

    // Student CRUD
    fun addNewStudent(
        name: String,
        levelCode: String,
        stream: String = "NONE",
        schoolName: String = "",
        avatarEmoji: String = "👦🏻",
        colorHex: String = "#00695C"
    ) {
        viewModelScope.launch {
            val count = allStudents.value.size
            if (count >= 1 && !_isPremiumUser()) {
                // Freemium limit prompt
                _showProDialog.value = true
            }
            val newId = repository.addStudent(name, levelCode, stream, schoolName, avatarEmoji, colorHex)
            selectStudent(newId)
            // Apply standard curriculum template
            repository.applyAlgerianTemplate(newId, levelCode)
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.deleteStudent(student)
            val remaining = allStudents.value.filter { it.id != student.id }
            if (remaining.isNotEmpty()) {
                selectStudent(remaining.first().id)
            }
        }
    }

    // Timetable Actions
    fun addTimetableSlot(
        dayIndex: Int,
        periodOrder: Int,
        startTime: String,
        endTime: String,
        subjectId: Long,
        roomOrTeacher: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val student = activeStudent.value ?: return@launch
            repository.addTimetableSlot(
                TimetableSlotEntity(
                    studentId = student.id,
                    dayOfWeek = dayIndex,
                    periodOrder = periodOrder,
                    startTime = startTime,
                    endTime = endTime,
                    subjectId = subjectId,
                    roomOrTeacher = roomOrTeacher,
                    customNotes = notes
                )
            )
        }
    }

    fun deleteTimetableSlot(slotId: Long) {
        viewModelScope.launch {
            repository.deleteTimetableSlot(slotId)
        }
    }

    fun resetToAlgerianTemplate(levelCode: String) {
        viewModelScope.launch {
            val student = activeStudent.value ?: return@launch
            repository.applyAlgerianTemplate(student.id, levelCode)
        }
    }

    // Homework Actions
    fun addHomework(
        subjectId: Long,
        title: String,
        description: String,
        dueDateMillis: Long,
        priority: Priority
    ) {
        viewModelScope.launch {
            val student = activeStudent.value ?: return@launch
            repository.addHomework(
                HomeworkEntity(
                    studentId = student.id,
                    subjectId = subjectId,
                    title = title,
                    description = description,
                    dueDateMillis = dueDateMillis,
                    priority = priority.name
                )
            )
        }
    }

    fun toggleHomework(homeworkId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleHomeworkCompletion(homeworkId, isCompleted)
        }
    }

    fun deleteHomework(homeworkId: Long) {
        viewModelScope.launch {
            repository.deleteHomework(homeworkId)
        }
    }

    // Exam Actions
    fun addExam(
        subjectId: Long,
        title: String,
        examType: ExamType,
        examDateMillis: Long,
        durationMinutes: Int,
        syllabus: String,
        notes: String
    ) {
        viewModelScope.launch {
            val student = activeStudent.value ?: return@launch
            repository.addExam(
                ExamEntity(
                    studentId = student.id,
                    subjectId = subjectId,
                    title = title,
                    examType = examType.name,
                    examDateMillis = examDateMillis,
                    durationMinutes = durationMinutes,
                    syllabusTopics = syllabus,
                    notes = notes
                )
            )
        }
    }

    fun updateExamRevision(examId: Long, status: RevisionStatus) {
        viewModelScope.launch {
            repository.updateExamRevisionStatus(examId, status)
        }
    }

    fun deleteExam(examId: Long) {
        viewModelScope.launch {
            repository.deleteExam(examId)
        }
    }

    // 🎒 Backpack Signature Feature Actions
    fun toggleBackpackItem(itemId: Long, isPacked: Boolean) {
        viewModelScope.launch {
            repository.toggleBackpackItem(itemId, isPacked)
        }
    }

    fun markAllBackpackReady() {
        viewModelScope.launch {
            val student = activeStudent.value ?: return@launch
            repository.markAllBackpackPacked(student.id, true)
            _showCelebration.value = true
        }
    }

    fun resetBackpackForTomorrow() {
        viewModelScope.launch {
            val student = activeStudent.value ?: return@launch
            repository.markAllBackpackPacked(student.id, false)
        }
    }

    fun addCustomBackpackItem(name: String, category: BackpackCategory) {
        viewModelScope.launch {
            val student = activeStudent.value ?: return@launch
            repository.addBackpackItem(
                BackpackItemEntity(
                    studentId = student.id,
                    itemName = name,
                    category = category.name,
                    isDefaultDaily = true,
                    isPacked = false
                )
            )
        }
    }

    // AI Study Assistant Chat
    fun askAiAssistant(question: String) {
        if (question.isBlank()) return
        val student = activeStudent.value ?: return

        // Add user message
        val userMsg = ChatMessage(sender = "USER", text = question)
        _aiChatMessages.value = _aiChatMessages.value + userMsg
        _isAiLoading.value = true

        viewModelScope.launch {
            val tomorrowSlots = tomorrowSummary.value?.slots ?: emptyList()
            val today = todaySlots.value
            val pendingHw = homeworkList.value.filter { !it.homework.isCompleted }
            val upcomingEx = examsList.value
            val subjects = allSubjects.value

            val answer = aiService.queryAssistant(
                student = student,
                question = question,
                tomorrowSlots = tomorrowSlots,
                todaySlots = today,
                pendingHomework = pendingHw,
                upcomingExams = upcomingEx,
                allSubjects = subjects
            )

            _isAiLoading.value = false
            _aiChatMessages.value = _aiChatMessages.value + ChatMessage(sender = "AI", text = answer)
        }
    }

    // OCR / Image Timetable Scanner
    fun scanTimetableWithAi(bitmap: Bitmap?, rawText: String?) {
        val student = activeStudent.value ?: return
        _isOcrScanning.value = true
        _ocrDraftSlots.value = emptyList()

        viewModelScope.launch {
            val drafts = aiService.parseTimetableImage(
                bitmap = bitmap,
                rawText = rawText,
                studentLevel = student.levelCode,
                knownSubjects = allSubjects.value
            )
            _ocrDraftSlots.value = drafts
            _isOcrScanning.value = false
        }
    }

    fun confirmOcrDrafts(drafts: List<ParsedSlotDraft>) {
        val student = activeStudent.value ?: return
        viewModelScope.launch {
            repository.clearTimetable(student.id)
            val subjects = allSubjects.value
            val entitySlots = drafts.map { draft ->
                val matchedSubject = subjects.find {
                    it.nameAr.contains(draft.subjectName, ignoreCase = true) ||
                    draft.subjectName.contains(it.shortCode, ignoreCase = true)
                } ?: subjects.firstOrNull() ?: SubjectEntity(
                    id = 1,
                    nameAr = draft.subjectName,
                    nameFr = draft.subjectName,
                    shortCode = draft.subjectName,
                    colorHex = "#00695C",
                    iconName = "menu_book",
                    defaultItemsAr = "كراس + كتاب"
                )

                TimetableSlotEntity(
                    studentId = student.id,
                    dayOfWeek = draft.dayIndex,
                    periodOrder = draft.periodOrder,
                    startTime = draft.startTime,
                    endTime = draft.endTime,
                    subjectId = matchedSubject.id,
                    roomOrTeacher = draft.roomOrTeacher
                )
            }
            repository.addTimetableSlots(entitySlots)
            _ocrDraftSlots.value = emptyList()
        }
    }

    fun clearOcrDrafts() {
        _ocrDraftSlots.value = emptyList()
    }

    private fun _isPremiumUser(): Boolean = true // Full access enabled for optimal parent experience
}
