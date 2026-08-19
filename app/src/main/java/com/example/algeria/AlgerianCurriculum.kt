package com.example.algeria

import com.example.data.model.AlgerianHoliday
import com.example.data.model.SubjectEntity

object AlgerianCurriculum {

    val OFFICIAL_SUBJECTS = listOf(
        SubjectEntity(
            id = 1,
            nameAr = "اللغة العربية",
            nameFr = "Langue Arabe",
            shortCode = "عربية",
            colorHex = "#16A34A", // Green
            iconName = "menu_book",
            defaultItemsAr = "كتاب القراءة والنصوص,كراس القسم (96 صفحة),كراس المحاولات",
            coefficient = 3.0,
            applicableCycles = "PRIMARY,MIDDLE,SECONDARY"
        ),
        SubjectEntity(
            id = 2,
            nameAr = "الرياضيات",
            nameFr = "Mathématiques",
            shortCode = "رياضيات",
            colorHex = "#2563EB", // Blue
            iconName = "calculate",
            defaultItemsAr = "كتاب الرياضيات,كراس الأنشطة (120 صفحة),أدوات الهندسة (مسطرة، كوس، منقلة، مدور),آلة حاسبة علمية",
            coefficient = 4.0,
            applicableCycles = "PRIMARY,MIDDLE,SECONDARY"
        ),
        SubjectEntity(
            id = 3,
            nameAr = "التربية الإسلامية",
            nameFr = "Éducation Islamique",
            shortCode = "إسلامية",
            colorHex = "#0D9488", // Teal
            iconName = "mosque",
            defaultItemsAr = "كتاب التربية الإسلامية,كراس الدروس (64 صفحة),المصحف الشريف",
            coefficient = 2.0,
            applicableCycles = "PRIMARY,MIDDLE,SECONDARY"
        ),
        SubjectEntity(
            id = 4,
            nameAr = "اللغة الفرنسية",
            nameFr = "Français",
            shortCode = "فرنسية",
            colorHex = "#9333EA", // Purple
            iconName = "translate",
            defaultItemsAr = "Manuel de Français,Cahier de cours (96 pages),Cahier d'activités",
            coefficient = 3.0,
            applicableCycles = "PRIMARY,MIDDLE,SECONDARY"
        ),
        SubjectEntity(
            id = 5,
            nameAr = "اللغة الإنجليزية",
            nameFr = "English",
            shortCode = "إنجليزية",
            colorHex = "#EA580C", // Orange
            iconName = "language",
            defaultItemsAr = "English Coursebook,Activity Book,Notebook (96 pages)",
            coefficient = 2.0,
            applicableCycles = "PRIMARY,MIDDLE,SECONDARY"
        ),
        SubjectEntity(
            id = 6,
            nameAr = "العلوم الطبيعية والحياة",
            nameFr = "Sciences Naturelles (SVT)",
            shortCode = "علوم",
            colorHex = "#059669", // Emerald
            iconName = "biotech",
            defaultItemsAr = "كتاب العلوم الطبيعية,كراس الدروس والتجارب (120 صفحة),مئزر أبيض قطني,أقلام ملونة للتخطيط العلمي",
            coefficient = 3.0,
            applicableCycles = "PRIMARY,MIDDLE,SECONDARY"
        ),
        SubjectEntity(
            id = 7,
            nameAr = "العلوم الفيزيائية والتكنولوجيا",
            nameFr = "Physique & Chimie",
            shortCode = "فيزياء",
            colorHex = "#0284C7", // Sky blue
            iconName = "science",
            defaultItemsAr = "كتاب الفيزياء,كراس الأعمال التطبيقية,مئزر أبيض,آلة حاسبة",
            coefficient = 3.0,
            applicableCycles = "MIDDLE,SECONDARY"
        ),
        SubjectEntity(
            id = 8,
            nameAr = "التاريخ والجغرافيا",
            nameFr = "Histoire & Géographie",
            shortCode = "اجتماعيات",
            colorHex = "#D97706", // Amber
            iconName = "public",
            defaultItemsAr = "كتاب التاريخ والجغرافيا,كراس الدروس (96 صفحة),أطلس العالم والجزائر",
            coefficient = 2.0,
            applicableCycles = "PRIMARY,MIDDLE,SECONDARY"
        ),
        SubjectEntity(
            id = 9,
            nameAr = "التربية المدنية",
            nameFr = "Éducation Civique",
            shortCode = "مدنية",
            colorHex = "#4F46E5", // Indigo
            iconName = "gavel",
            defaultItemsAr = "كتاب التربية المدنية,كراس الدروس (64 صفحة)",
            coefficient = 1.0,
            applicableCycles = "PRIMARY,MIDDLE,SECONDARY"
        ),
        SubjectEntity(
            id = 10,
            nameAr = "التربية البدنية والرياضية",
            nameFr = "Éducation Physique (EPS)",
            shortCode = "رياضة بدنية",
            colorHex = "#E11D48", // Rose Red
            iconName = "fitness_center",
            defaultItemsAr = "بدلة رياضية مناسبة,حذاء رياضي,قارورة ماء شخصية,منشفة صغيرة",
            coefficient = 1.0,
            applicableCycles = "PRIMARY,MIDDLE,SECONDARY"
        ),
        SubjectEntity(
            id = 11,
            nameAr = "التربية التشكيلية والفنية",
            nameFr = "Éducation Artistique",
            shortCode = "رسم / فنون",
            colorHex = "#DB2777", // Pink
            iconName = "palette",
            defaultItemsAr = "كراس الرسم والتلوين (حجم كبير),أقلام لبدية وخشبية ملونة,مقص غير حاد وغراء",
            coefficient = 1.0,
            applicableCycles = "PRIMARY,MIDDLE"
        ),
        SubjectEntity(
            id = 12,
            nameAr = "التربية الموسيقية",
            nameFr = "Éducation Musicale",
            shortCode = "موسيقى",
            colorHex = "#7C3AED", // Violet
            iconName = "music_note",
            defaultItemsAr = "كراس الموسيقى المخطط,آلة الفلوت (Flûte)",
            coefficient = 1.0,
            applicableCycles = "MIDDLE"
        ),
        SubjectEntity(
            id = 13,
            nameAr = "الفلسفة",
            nameFr = "Philosophie",
            shortCode = "فلسفة",
            colorHex = "#B45309", // Warm Brown
            iconName = "psychology",
            defaultItemsAr = "كتاب الفلسفة والنصوص الفكرية,كراس المحاضرات (192 صفحة)",
            coefficient = 4.0,
            applicableCycles = "SECONDARY"
        ),
        SubjectEntity(
            id = 14,
            nameAr = "التكنولوجيا وهندسة الطرائق",
            nameFr = "Génie des Procédés / Mécanique",
            shortCode = "تكنولوجيا",
            colorHex = "#475569", // Slate
            iconName = "engineering",
            defaultItemsAr = "كراس الهندسة والتكنولوجيا,مئزر أبيض,أدوات الرسم التقني",
            coefficient = 4.0,
            applicableCycles = "SECONDARY"
        ),
        SubjectEntity(
            id = 15,
            nameAr = "التسيير المالي والمحاسبي",
            nameFr = "Comptabilité & Gestion",
            shortCode = "محاسبة",
            colorHex = "#0891B2", // Cyan
            iconName = "account_balance",
            defaultItemsAr = "مدونة المخطط الوطني المحاسبي (SCF),كراس التمارين,آلة حاسبة",
            coefficient = 4.0,
            applicableCycles = "SECONDARY"
        )
    )

    val ALGERIAN_HOLIDAYS = listOf(
        AlgerianHoliday(
            nameAr = "عطلة الخريف",
            nameFr = "Vacances d'Automne",
            startDate = "2024-10-29",
            endDate = "2024-11-03",
            seasonBadge = "خريف",
            iconEmoji = "🍂"
        ),
        AlgerianHoliday(
            nameAr = "عطلة الشتاء",
            nameFr = "Vacances d'Hiver",
            startDate = "2024-12-19",
            endDate = "2024-01-05",
            seasonBadge = "شتاء",
            iconEmoji = "❄️"
        ),
        AlgerianHoliday(
            nameAr = "عطلة الربيع",
            nameFr = "Vacances de Printemps",
            startDate = "2025-03-20",
            endDate = "2025-04-06",
            seasonBadge = "ربيع",
            iconEmoji = "🌸"
        ),
        AlgerianHoliday(
            nameAr = "عطلة الصيف الرسمية",
            nameFr = "Vacances d'Été",
            startDate = "2025-07-03",
            endDate = "2025-09-15",
            seasonBadge = "صيف",
            iconEmoji = "☀️"
        ),
        AlgerianHoliday(
            nameAr = "عيد الثورة (01 نوفمبر)",
            nameFr = "Fête de la Révolution",
            startDate = "2024-11-01",
            endDate = "2024-11-01",
            seasonBadge = "عطلة وطنية",
            iconEmoji = "🇩🇿"
        ),
        AlgerianHoliday(
            nameAr = "رأس السنة الأمازيغية (يناير - 12 جانفي)",
            nameFr = "Yennayer",
            startDate = "2025-01-12",
            endDate = "2025-01-12",
            seasonBadge = "عطلة رسمية",
            iconEmoji = "ⵣ"
        ),
        AlgerianHoliday(
            nameAr = "عيد الفطر المبارك",
            nameFr = "Aïd El Fitr",
            startDate = "2025-03-30",
            endDate = "2025-04-01",
            seasonBadge = "عطلة دينية",
            iconEmoji = "🌙"
        ),
        AlgerianHoliday(
            nameAr = "عيد الاستقلال (05 جويلية)",
            nameFr = "Fête de l'Indépendance",
            startDate = "2025-07-05",
            endDate = "2025-07-05",
            seasonBadge = "عطلة وطنية",
            iconEmoji = "🇩🇿"
        )
    )

    // Standard backpack default must-haves
    val DEFAULT_BACKPACK_ESSENTIALS = listOf(
        "المقلمة كاملة (أقلام زرقاء، خضراء، رصاص، ممحاة، مبراة)",
        "كراس المراسلة والغيابات (دفتر المتابعة)",
        "قارورة ماء شخصية ولمجة صحية",
        "بطاقة الدخول المدرسي والمئزر المدرسي",
        "مناديل ورقية / معقم اليدين"
    )
}
