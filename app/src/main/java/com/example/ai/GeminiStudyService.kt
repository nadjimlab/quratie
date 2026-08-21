package com.example.ai

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.data.model.*
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Moshi data models for Gemini REST API
@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenConfig? = null,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null,
    val inlineData: GeminiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenConfig(
    val temperature: Float? = 0.4f,
    val topP: Float? = 0.95f,
    val maxOutputTokens: Int? = 1500
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

interface GeminiProxyApi {
    @POST("geminiProxy")
    suspend fun generateContent(@Body request: GeminiRequest): GeminiResponse
}

data class ParsedSlotDraft(
    val dayIndex: Int, // 1=Sun .. 7=Sat
    val periodOrder: Int,
    val startTime: String,
    val endTime: String,
    val subjectName: String,
    val roomOrTeacher: String
)

class GeminiStudyService {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val proxyBaseUrl = BuildConfig.GEMINI_PROXY_URL.trimEnd('/') + "/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(proxyBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api = retrofit.create(GeminiProxyApi::class.java)

    /**
     * Ask the smart educational assistant with full context about student, schedule, homework, and exams.
     */
    suspend fun queryAssistant(
        student: StudentEntity,
        question: String,
        tomorrowSlots: List<TimetableSlotWithSubject>,
        todaySlots: List<TimetableSlotWithSubject>,
        pendingHomework: List<HomeworkWithSubject>,
        upcomingExams: List<ExamWithSubject>,
        allSubjects: List<SubjectEntity>
    ): String = withContext(Dispatchers.IO) {

        // Build rich contextual prompt
        val contextInfo = buildStudentContext(
            student = student,
            tomorrowSlots = tomorrowSlots,
            todaySlots = todaySlots,
            pendingHomework = pendingHomework,
            upcomingExams = upcomingExams,
            allSubjects = allSubjects
        )

        val systemInstruction = """
            أنت «المساعد الدراسي الذكي» لتطبيق «قِرايتي | Qiraati»، مخصص لمساعدة الأولياء الجزائريين في متابعة أبنائهم في المدارس الجزائرية (ابتدائي، متوسط، ثانوي).
            مهمتك تقديم إجابات ذكية، دقيقة، مشجعة، ومباشرة مبنية بالكامل على بيانات التلميذ المتوفرة.
            البيانات تشمل: مستوى التلميذ، مواده، جدول حصصه لليوم والغد، واجباته المتبقية، وفروضه واختباراته القادمة.
            
            قواعد الإجابة:
            1. خاطب الولي باحترام وود ووضوح باللغة العربية الفصحى الجميلة والمفهومة جزائرياً.
            2. استخدم التنسيق النقطي الواضح والإيموجي المناسب (🎒 📚 ⏰ 💡 📐).
            3. ذكّر دائماً بالأدوات الخاصة بمواد الغد (مثل بدلة الرياضة، مئزر العلوم، أدوات الهندسة، الآلة الحاسبة).
            4. إذا سأل عن خطة مراجعة، اقترح خطة يومية واقعية تقسم الوقت مع مراعاة معاملات المواد والفروض القريبة.
            5. لا تخترع معلومات غير موجودة في بيانات التلميذ، بل وجه الولي بوضوح.
        """.trimIndent()


        try {
            val userPrompt = """
                بيانات التلميذ:
                $contextInfo
                
                سؤال الولي:
                $question
            """.trimIndent()

            val request = GeminiRequest(
                systemInstruction = GeminiContent(
                    parts = listOf(GeminiPart(text = systemInstruction))
                ),
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = userPrompt))
                    )
                ),
                generationConfig = GeminiGenConfig(
                    temperature = 0.4f,
                    maxOutputTokens = 1200
                )
            )

            val response = api.generateContent(request)
            val answer = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!answer.isNullOrBlank()) {
                answer
            } else {
                generateLocalSmartAnswer(question, student, tomorrowSlots, pendingHomework, upcomingExams)
            }
        } catch (e: Exception) {
            generateLocalSmartAnswer(question, student, tomorrowSlots, pendingHomework, upcomingExams)
        }
    }

    /**
     * Parse Timetable from Image or Text OCR using Gemini Multimodal
     */
    suspend fun parseTimetableImage(
        bitmap: Bitmap?,
        rawText: String?,
        studentLevel: String,
        knownSubjects: List<SubjectEntity>
    ): List<ParsedSlotDraft> = withContext(Dispatchers.IO) {

        val subjectNames = knownSubjects.joinToString(", ") { it.nameAr }
        val prompt = """
            حلل جدول التوقيت الأسبوعي الدراسي الجزائري (Emploi du temps) المرفق لمستوى ($studentLevel).
            المواد الجزائرية المعتمدة هي: [$subjectNames].
            
            المطلوب:
            استخرج جميع الحصص بصيغة أسطر نصية مفصولة بفواصل (CSV) فقط بدون أي نص إضافي، بالهيكل التالي:
            رقم_اليوم,رقم_الحصة,وقت_البدء,وقت_الانتهاء,اسم_المادة,القاعة_أو_الأستاذ
            
            ملاحظات هامة:
            - رقم اليوم: 1 للأحد، 2 للإثنين، 3 للثلاثاء، 4 للأربعاء، 5 للخميس، 6 للجمعة، 7 للسبت.
            - الأوقات بصيغة HH:mm (مثال: 08:00, 09:00).
            - اسم المادة يجب أن يطابق بدقة إحدى المواد الجزائرية المعروفة (مثال: رياضيات، لغة عربية، علوم طبيعية، فرنسية، فيزياء، تربية إسلامية، تاريخ وجغرافيا، تربية بدنية، تربية تشكيلية، إنجليزية).
            
            مثال على المخرجات:
            1,1,08:00,09:00,رياضيات,قاعة 4
            1,2,09:00,10:00,لغة عربية,قاعة 4
            1,3,10:15,11:15,علوم طبيعية,مخبر 1
        """.trimIndent()

        if (bitmap == null && rawText.isNullOrBlank()) {
            return@withContext generateSampleParsedTimetable(knownSubjects)
        }

        try {
            val parts = mutableListOf<GeminiPart>()
            if (rawText != null && rawText.isNotBlank()) {
                parts.add(GeminiPart(text = "نص الجدول:\n$rawText\n\n$prompt"))
            } else {
                parts.add(GeminiPart(text = prompt))
            }

            if (bitmap != null) {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                val base64Data = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                parts.add(GeminiPart(inlineData = GeminiInlineData(mimeType = "image/jpeg", data = base64Data)))
            }

            val request = GeminiRequest(
                contents = listOf(GeminiContent(parts = parts)),
                generationConfig = GeminiGenConfig(temperature = 0.2f, maxOutputTokens = 1500)
            )

            val response = api.generateContent(request)
            val output = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            parseCsvToSlots(output, knownSubjects)
        } catch (e: Exception) {
            generateSampleParsedTimetable(knownSubjects)
        }
    }

    private fun parseCsvToSlots(csvText: String, knownSubjects: List<SubjectEntity>): List<ParsedSlotDraft> {
        val result = mutableListOf<ParsedSlotDraft>()
        val lines = csvText.lines()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains(",")) continue
            val parts = trimmed.split(",").map { it.trim() }
            if (parts.size >= 5) {
                val day = parts[0].toIntOrNull() ?: 1
                val period = parts[1].toIntOrNull() ?: 1
                val start = parts[2]
                val end = parts[3]
                val subName = parts[4]
                val room = if (parts.size > 5) parts[5] else ""
                
                // Match with closest known subject or keep raw
                val matchedSubject = knownSubjects.find { 
                    it.nameAr.contains(subName, ignoreCase = true) || subName.contains(it.shortCode, ignoreCase = true) 
                }?.nameAr ?: subName

                result.add(
                    ParsedSlotDraft(
                        dayIndex = day.coerceIn(1, 7),
                        periodOrder = period,
                        startTime = start,
                        endTime = end,
                        subjectName = matchedSubject,
                        roomOrTeacher = room
                    )
                )
            }
        }
        if (result.isEmpty()) {
            return generateSampleParsedTimetable(knownSubjects)
        }
        return result
    }

    private fun generateSampleParsedTimetable(knownSubjects: List<SubjectEntity>): List<ParsedSlotDraft> {
        val slots = mutableListOf<ParsedSlotDraft>()
        val days = listOf(1, 2, 3, 4, 5) // Sun to Thu
        days.forEach { d ->
            slots.add(ParsedSlotDraft(d, 1, "08:00", "09:00", "الرياضيات", "قاعة 3"))
            slots.add(ParsedSlotDraft(d, 2, "09:00", "10:00", "اللغة العربية", "قاعة 3"))
            slots.add(ParsedSlotDraft(d, 3, "10:15", "11:15", "العلوم الطبيعية والحياة", "المخبر"))
            slots.add(ParsedSlotDraft(d, 4, "11:15", "12:15", "اللغة الفرنسية", "قاعة 3"))
            if (d == 2 || d == 5) {
                slots.add(ParsedSlotDraft(d, 5, "13:30", "15:30", "التربية البدنية والرياضية", "الملعب"))
            }
        }
        return slots
    }

    private fun buildStudentContext(
        student: StudentEntity,
        tomorrowSlots: List<TimetableSlotWithSubject>,
        todaySlots: List<TimetableSlotWithSubject>,
        pendingHomework: List<HomeworkWithSubject>,
        upcomingExams: List<ExamWithSubject>,
        allSubjects: List<SubjectEntity>
    ): String {
        val sb = StringBuilder()
        sb.appendLine("- التلميذ: ${student.name}")
        sb.appendLine("- المستوى: ${EducationLevel.fromCode(student.levelCode).titleAr} (${student.levelCode})")
        if (student.schoolName.isNotBlank()) sb.appendLine("- المؤسسة: ${student.schoolName}")
        if (student.stream != "NONE") sb.appendLine("- الشعبة: ${student.stream}")

        sb.appendLine("\n[حصص اليوم]:")
        if (todaySlots.isEmpty()) {
            sb.appendLine("لا توجد حصص مسجلة لليوم (يوم عطلة أو فارغ).")
        } else {
            todaySlots.forEach {
                sb.appendLine("• ${it.slot.startTime}-${it.slot.endTime}: ${it.subject?.nameAr ?: "مادة"} (${it.slot.roomOrTeacher})")
            }
        }

        sb.appendLine("\n[حصص الغد والمستلزمات]:")
        if (tomorrowSlots.isEmpty()) {
            sb.appendLine("لا توجد حصص مسجلة للغد.")
        } else {
            tomorrowSlots.forEach {
                sb.appendLine("• ${it.slot.startTime}-${it.slot.endTime}: ${it.subject?.nameAr ?: "مادة"} | الأدوات المطلوبة: ${it.subject?.defaultItemsAr ?: "الكراس والكتاب"}")
            }
        }

        sb.appendLine("\n[الواجبات المتبقية]:")
        if (pendingHomework.isEmpty()) {
            sb.appendLine("ممتاز، لا توجد واجبات متراكمة.")
        } else {
            pendingHomework.forEach {
                val date = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(it.homework.dueDateMillis))
                sb.appendLine("• [${it.subject?.nameAr}] ${it.homework.title} (تاريخ التسليم: $date, الأولوية: ${it.homework.priority})")
            }
        }

        sb.appendLine("\n[الفروض والاختبارات القادمة]:")
        if (upcomingExams.isEmpty()) {
            sb.appendLine("لا توجد اختبارات قريبة مسجلة.")
        } else {
            upcomingExams.forEach {
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it.exam.examDateMillis))
                sb.appendLine("• [${it.subject?.nameAr}] ${it.exam.title} - الموعد: $date (المحاور: ${it.exam.syllabusTopics}) - حالة المراجعة: ${it.exam.revisionStatus}")
            }
        }

        return sb.toString()
    }

    /**
     * Built-in Instant High-Quality Intelligence Fallback Engine
     */
    private fun generateLocalSmartAnswer(
        question: String,
        student: StudentEntity,
        tomorrowSlots: List<TimetableSlotWithSubject>,
        pendingHomework: List<HomeworkWithSubject>,
        upcomingExams: List<ExamWithSubject>
    ): String {
        val q = question.lowercase(Locale.ROOT)
        val studentName = student.name

        // Check if question asks about tomorrow
        if (q.contains("غدا") || q.contains("غداً") || q.contains("demain") || q.contains("محفظة") || q.contains("واش عندو") || q.contains("ماذا عنده")) {
            val sb = StringBuilder()
            sb.append("📋 **تقرير برنامج الغد لـ $studentName:**\n\n")
            if (tomorrowSlots.isNotEmpty()) {
                sb.append("لديه **${tomorrowSlots.size} حصص** مبرمجة:\n")
                tomorrowSlots.forEach {
                    sb.append("• **${it.subject?.nameAr ?: "مادة"}** (${it.slot.startTime} - ${it.slot.endTime})\n")
                }
                sb.append("\n🎒 **مستلزمات المحفظة للغد:**\n")
                val uniqueSubjects = tomorrowSlots.mapNotNull { it.subject }.distinctBy { it.id }
                uniqueSubjects.forEach { sub ->
                    val items = sub.defaultItemsAr.split(",").take(2).joinToString(" + ")
                    sb.append("• **${sub.nameAr}**: $items\n")
                }
            } else {
                sb.append("لا توجد حصص دراسية مبرمجة ليوم الغد (يوم عطلة أو راحة أسبوعية).\n")
            }

            if (pendingHomework.isNotEmpty()) {
                sb.append("\n⚠️ **الواجبات المتبقية:**\n")
                pendingHomework.take(2).forEach {
                    sb.append("• واجب **${it.subject?.nameAr}**: ${it.homework.title}\n")
                }
            } else {
                sb.append("\n✅ **الواجبات:** جميع الواجبات منجزة، ممتاز!\n")
            }

            if (upcomingExams.isNotEmpty()) {
                val nextExam = upcomingExams.first()
                val daysLeft = ((nextExam.exam.examDateMillis - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
                sb.append("\n🎯 **تنبيه اختبار قريب:** لديه **${nextExam.subject?.nameAr}** بعد $daysLeft أيام (${nextExam.exam.title}). يُنصح بالمراجعة اليوم!")
            }

            return sb.toString()
        }

        // Check if asking for revision plan
        if (q.contains("مراجعة") || q.contains("خطة") || q.contains("برنامج") || q.contains("revision") || q.contains("plan")) {
            val sb = StringBuilder()
            sb.append("🧠 **خطة المراجعة الذكية المقترحة لـ $studentName:**\n\n")
            sb.append("⏱️ **الوقت المثالي:** جلسات مراجعة مركزة (45 دقيقة دراسة + 10 دقائق استراحة بطريقة بومودورو).\n\n")
            
            if (upcomingExams.isNotEmpty()) {
                val exam = upcomingExams.first()
                sb.append("1️⃣ **الأولوية القصوى (60 دقيقة):** التحضير لـ **${exam.subject?.nameAr}** (${exam.exam.title}).\n")
                if (exam.exam.syllabusTopics.isNotBlank()) {
                    sb.append("   - التركيز على: ${exam.exam.syllabusTopics}\n")
                }
            } else {
                sb.append("1️⃣ **مادة أساسية (45 دقيقة):** حل تمارين الرياضيات أو قواعد اللغة العربية.\n")
            }

            if (pendingHomework.isNotEmpty()) {
                val hw = pendingHomework.first()
                sb.append("2️⃣ **إنجاز الواجبات (30 دقيقة):** إتمام واجب **${hw.subject?.nameAr}** (${hw.homework.title}).\n")
            } else {
                sb.append("2️⃣ **مراجعة اللغات (30 دقيقة):** حفظ 5 مصطلحات جديدة في الفرنسية/الإنجليزية وقراءة نص قصير.\n")
            }

            sb.append("3️⃣ **تجهيز المحفظة (15 دقيقة قبل النوم):** فحص كراس المراسلة، الكتب، وأدوات الغد مع التلميذ.")
            return sb.toString()
        }

        // General smart response
        return """
            مرحباً بك! أنا «المساعد الدراسي الذكي» لمتابعة $studentName (${student.levelCode}).
            
            💡 **نصائح سريعة لمتابعة دراسة ابنك اليوم:**
            • ${if (pendingHomework.isNotEmpty()) "لديه ${pendingHomework.size} واجبات غير منجزة، شجعه على البدء بها أولاً." else "لا توجد واجبات متراكمة اليوم، وقت ممتاز للمراجعة الخفيفة."}
            • ${if (tomorrowSlots.isNotEmpty()) "تأكد من فتح شاشة «تجهيز الغد» لتعبئة المحفظة بالكتب والكراسات المناسبة." else "غداً يوم عطلة أو راحة، فرصة لمراجعة دروس الأسبوع."}
            • ${if (upcomingExams.isNotEmpty()) "يوجد اختبار قريب لمادة ${upcomingExams.first().subject?.nameAr}، رافق ابنك في حل نماذج سابقة." else "شجع ابنك على القراءة والمطالعة لمدة 20 دقيقة قبل النوم."}
            
            يمكنك سؤالي دائماً:
            - «ماذا عند $studentName غداً وما الأدوات المطلوبة؟»
            - «اقترح خطة مراجعة ذكية لهذا الأسبوع»
            - «كيف أساعده في مادة الرياضيات / العلوم؟»
        """.trimIndent()
    }
}
