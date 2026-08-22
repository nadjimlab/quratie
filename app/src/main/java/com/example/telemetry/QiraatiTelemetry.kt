package com.example.telemetry

import android.content.Context
import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

object QiraatiTelemetry {
    private const val PREFS = "qiraati_telemetry"
    private const val ANONYMOUS_ID = "anonymous_id"
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    fun record(context: Context, eventType: String) {
        if (eventType != "app_open" && eventType != "session_start") return
        val endpoint = BuildConfig.QIRAATI_TELEMETRY_URL.trim()
        if (!endpoint.startsWith("https://")) return

        val anonymousId = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(ANONYMOUS_ID, null)
            ?: UUID.randomUUID().toString().also {
                context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putString(ANONYMOUS_ID, it)
                    .apply()
            }

        val payload = JSONObject()
            .put("eventType", eventType)
            .put("anonymousId", anonymousId)
            .put("appVersion", BuildConfig.VERSION_NAME)
            .put("platform", "android")
            .toString()

        val request = Request.Builder()
            .url(endpoint)
            .post(payload.toRequestBody(jsonType))
            .header("Accept", "application/json")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) = Unit
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.close()
            }
        })
    }
}
