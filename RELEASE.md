# Release AAB لـ Google Play

يُبنى ملف Release فقط عبر Workflow باسم **Android Release Bundle**. لا يُحفظ ملف JKS أو أي كلمة مرور في المستودع.

## GitHub Secrets المطلوبة

أضف هذه القيم من **Settings → Secrets and variables → Actions**:

| Secret | القيمة |
|---|---|
| `ANDROID_KEYSTORE_BASE64` | ملف `qiraati-upload.jks` بعد تحويله إلى Base64 |
| `ANDROID_KEYSTORE_PASSWORD` | كلمة مرور مخزن JKS |
| `ANDROID_KEY_PASSWORD` | كلمة مرور المفتاح داخله |

اسم المفتاح داخل Gradle هو `upload`. إذا استُخدم اسم مختلف، يجب تحديث `signingConfigs.release` في `app/build.gradle.kts`.

## تشغيل البناء

يمكن تشغيله يدويًا من تبويب Actions عبر **Android Release Bundle → Run workflow**، أو إنشاء tag يبدأ بـ `v`:

```bash
git tag v1.0.0
git push origin v1.0.0
```

بعد نجاح التشغيل، نزّل Artifact باسم `qiraati-release-aab-*` وارفع ملف `.aab` إلى مسار **Internal testing** في Play Console.

## ملاحظات أمنية

لا ترسل ملف JKS أو قيم Secrets في Issues أو Pull Requests أو المحادثات. احتفظ بنسخة احتياطية مشفرة من المفتاح. لا تغيّر `applicationId` بعد أول نشر للتطبيق. ارفع `versionCode` في كل إصدار جديد.

يجب تفعيل Google Play App Signing عند أول رفع، حتى يكون مفتاح توقيع التوزيع محفوظًا لدى Google، بينما يبقى مفتاح الرفع لديك ويمكن تغييره عند الضرورة.
