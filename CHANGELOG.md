# سجل التغييرات

## [Unreleased] — 2026-08-21

### النشر العام خارج Google Play

- إضافة `.github/workflows/android-public-release.yml` لبناء APK Release موقّع ونشره تلقائيًا في GitHub Releases عند استخدام Tag يبدأ بـ `v`.
- إضافة `update.json` إلى كل إصدار عام ليستعمله `UpdateChecker` داخل التطبيق.
- إرفاق SHA-256 للـ APK مع كل إصدار للتحقق من سلامة الملف.
- الحفاظ على `com.qiraati.dzstudy` ومفتاح التوقيع الحالي.

### Firebase App Distribution

- إضافة `.github/workflows/firebase-app-distribution.yml` لبناء APK Release موقّع وتوزيعه يدويًا على Firebase App Distribution.
- يعتمد Workflow على مفاتيح التوقيع الحالية ولا يغير `com.qiraati.dzstudy`.
- يتطلب إعداد أسرار `FIREBASE_SERVICE_ACCOUNT_JSON` و`FIREBASE_ANDROID_APP_ID` داخل بيئة GitHub `qiraati-upload`.

### أمان Gemini

- إزالة استدعاء Gemini المباشر من تطبيق Android وإزالة متغير `GEMINI_API_KEY` من ملف البيئة الخاص بالتطبيق.
- إضافة Cloud Function باسم `geminiProxy`؛ يستقبل طلبات النص والصورة ويستخدم سر `GEMINI_API_KEY` من Firebase Secret Manager.
- الحفاظ على عنوان proxy فقط داخل `BuildConfig`، مع إبقاء المفتاح الحقيقي خارج APK وخارج GitHub.
- إضافة توثيق نشر الوسيط في `functions/README.md`.

### الصلاحيات والخصوصية التقنية

- الإبقاء على `POST_NOTIFICATIONS` في Manifest وطلبه صراحةً على Android 13 وما بعده.
- التحقق من أن التقاط الجدول يستخدم `TakePicturePreview` ولا يطلب صلاحيات التخزين القديمة؛ لا توجد إضافة لـ `READ_EXTERNAL_STORAGE` أو `WRITE_EXTERNAL_STORAGE`.
- استمرار تعطيل النسخ الاحتياطي السحابي لبيانات التلميذ.

### فحص الإصدار داخل التطبيق

- إضافة `UpdateChecker` لقراءة `update.json` من GitHub Releases.
- مقارنة `versionCode` الحالي مع `versionCode` المنشور.
- عرض تنبيه عربي مع رابط Google Play، ودعم التحديث الإجباري أو الاختياري.
- إضافة `update.json.example` لتحديد صيغة ملف الإصدار.

### التوقيع والبناء

- التأكد من أن `release` يستخدم `signingConfigs.release` فقط.
- إبقاء إعداد Debug منفصلًا للاختبارات وعدم استخدامه في `bundleRelease`.
- عدم لمس `applicationId` الحالي أو مفتاح التوقيع الحالي.

### الاختبارات

- إضافة `QiraatiCoreInstrumentationTest` لاختبار إدخال وحفظ حصة في الجدول.
- إضافة اختبار إنشاء قناة الإشعارات المحلية وجدولة التذكير.
- إضافة اختبار حفظ وقراءة ملف التلميذ من قاعدة Room.

### الخصوصية

- إضافة `PRIVACY_POLICY.md` مستقل يشرح البيانات المحلية، الكاميرا، Gemini عبر الوسيط، الإشعارات، فحص الإصدار، وحماية بيانات القُصّر.

### Pull Requests

- تمت مراجعة Pull Requests المفتوحة السبعة وتصنيفها في تقرير التسليم. لم يتم دمج أو إغلاق أي Pull Request تلقائيًا لأن عدة PRs فشلت فحوصاتها وتحتاج تحققًا بعد التغييرات الحالية.
