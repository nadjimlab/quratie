package com.example.data.model

enum class EducationCycle(val titleAr: String, val titleFr: String) {
    PRIMARY("التعليم الابتدائي", "Enseignement Primaire"),
    MIDDLE("التعليم المتوسط", "Enseignement Moyen"),
    SECONDARY("التعليم الثانوي", "Enseignement Secondaire")
}

enum class EducationLevel(
    val code: String,
    val titleAr: String,
    val titleFr: String,
    val cycle: EducationCycle,
    val yearNum: Int
) {
    PRIMARY_1("1AP", "السنة الأولى ابتدائي", "1ère Année Primaire", EducationCycle.PRIMARY, 1),
    PRIMARY_2("2AP", "السنة الثانية ابتدائي", "2ème Année Primaire", EducationCycle.PRIMARY, 2),
    PRIMARY_3("3AP", "السنة الثالثة ابتدائي", "3ème Année Primaire", EducationCycle.PRIMARY, 3),
    PRIMARY_4("4AP", "السنة الرابعة ابتدائي", "4ème Année Primaire", EducationCycle.PRIMARY, 4),
    PRIMARY_5("5AP", "السنة الخامسة ابتدائي", "5ème Année Primaire", EducationCycle.PRIMARY, 5),

    MIDDLE_1("1AM", "السنة الأولى متوسط", "1ère Année Moyenne", EducationCycle.MIDDLE, 1),
    MIDDLE_2("2AM", "السنة الثانية متوسط", "2ème Année Moyenne", EducationCycle.MIDDLE, 2),
    MIDDLE_3("3AM", "السنة الثالثة متوسط", "3ème Année Moyenne", EducationCycle.MIDDLE, 3),
    MIDDLE_4("4AM", "السنة الرابعة متوسط (BEM)", "4ème Année Moyenne (BEM)", EducationCycle.MIDDLE, 4),

    SECONDARY_1("1AS", "السنة الأولى ثانوي", "1ère Année Secondaire", EducationCycle.SECONDARY, 1),
    SECONDARY_2("2AS", "السنة الثانية ثانوي", "2ème Année Secondaire", EducationCycle.SECONDARY, 2),
    SECONDARY_3("3AS", "السنة الثالثة ثانوي (بكالوريا BAC)", "3ème Année Secondaire (BAC)", EducationCycle.SECONDARY, 3);

    companion object {
        fun fromCode(code: String): EducationLevel {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: MIDDLE_3
        }
    }
}

enum class SecondaryStream(val titleAr: String, val titleFr: String) {
    NONE("بدون شعبة", "Tronc Commun"),
    COMMON_SCIENCE("جذع مشترك علوم وتكنولوجيا", "Tronc Commun Sciences & Tech"),
    COMMON_LETTERS("جذع مشترك آداب", "Tronc Commun Lettres"),
    EXPERIMENTAL_SCIENCES("شعبة علوم تجريبية", "Sciences Expérimentales"),
    MATHEMATICS("شعبة رياضيات", "Mathématiques"),
    MATH_TECH("شعبة تقني رياضي", "Technique Mathématiques"),
    MANAGEMENT_ECONOMICS("شعبة تسيير واقتصاد", "Gestion & Économie"),
    FOREIGN_LANGUAGES("شعبة لغات أجنبية", "Langues Étrangères"),
    LITERATURE_PHILOSOPHY("شعبة آداب وفلسفة", "Lettres & Philosophie")
}

enum class Priority(val titleAr: String, val titleFr: String) {
    LOW("عادي", "Normal"),
    MEDIUM("مهم", "Important"),
    HIGH("عاجل / هام جداً", "Urgent")
}

enum class ExamType(val titleAr: String, val titleFr: String, val defaultCoefficient: Double) {
    TEST_1("الفرض الأول", "Devoir 1", 1.0),
    TEST_2("الفرض الثاني", "Devoir 2", 1.0),
    TERM_EXAM("اختبار الفصل", "Examen de Trimestre", 2.0),
    RETAKE("امتحان استدراكي", "Rattrapage", 1.0),
    ORAL("تقويم مستمر / شفهي", "Évaluation continue", 1.0)
}

enum class RevisionStatus(val titleAr: String, val titleFr: String) {
    NOT_STARTED("لم أبدأ بعد", "Non commencé"),
    IN_PROGRESS("قيد المراجعة", "En cours"),
    COMPLETED("تمت المراجعة بنجاح", "Terminé")
}

enum class BackpackCategory(val titleAr: String, val titleFr: String) {
    BOOK("كتاب مدرسي", "Livre"),
    NOTEBOOK("كراس", "Cahier"),
    TOOL("أدوات هندسية / مستلزمات", "Fournitures"),
    CLOTHING("مئزر / بدلة رياضية", "Tenue / Tablier"),
    SNACK_HYGIENE("لمجة / قارورة ماء / معقم", "Goûter / Eau"),
    DOC("كراس المراسلة / بطاقة", "Documents")
}

enum class AlgerianDayOfWeek(val dayIndex: Int, val nameAr: String, val nameFr: String, val isSchoolDay: Boolean) {
    SUNDAY(1, "الأحد", "Dimanche", true),
    MONDAY(2, "الإثنين", "Lundi", true),
    TUESDAY(3, "الثلاثاء", "Mardi", true),
    WEDNESDAY(4, "الأربعاء", "Mercredi", true),
    THURSDAY(5, "الخميس", "Jeudi", true),
    FRIDAY(6, "الجمعة", "Vendredi", false),
    SATURDAY(7, "السبت", "Samedi", false);

    companion object {
        fun fromIndex(index: Int): AlgerianDayOfWeek {
            return entries.find { it.dayIndex == index } ?: SUNDAY
        }
        
        fun getTomorrow(todayIndex: Int): AlgerianDayOfWeek {
            val tomorrowIndex = if (todayIndex >= 7) 1 else todayIndex + 1
            return fromIndex(tomorrowIndex)
        }
    }
}
