# Qiraati Gemini proxy

The Android app must call the deployed `geminiProxy` HTTPS function. The Gemini key is stored only in Firebase Secret Manager and is never shipped in the APK.

```bash
cd functions
npm install
firebase functions:secrets:set GEMINI_API_KEY
firebase deploy --only functions:geminiProxy
```

After deployment, replace the non-secret `GEMINI_PROXY_URL` value in `app/build.gradle.kts` with the deployed HTTPS base URL. Do not commit the key, `.env` files, service-account JSON, or deployment tokens.

The function accepts the same `contents`, `systemInstruction`, and `generationConfig` request shape used by the app, and it supports text and `inlineData` image parts for timetable scanning.
