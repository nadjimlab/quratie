package com.example.update

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class UpdateManifest(
    val versionCode: Int,
    val versionName: String,
    val downloadUrl: String,
    val releaseNotes: String = "",
    val mandatory: Boolean = false
)

class UpdateChecker {
    companion object {
        private const val MANIFEST_URL =
            "https://github.com/nadjimyahiaoui1992-lab/quratie/releases/latest/download/update.json"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    private val adapter = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter(UpdateManifest::class.java)

    suspend fun check(): UpdateManifest? = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url(MANIFEST_URL)
                .header("Accept", "application/json")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@runCatching null
                val manifest = adapter.fromJson(response.body?.string().orEmpty())
                manifest?.takeIf { it.versionCode > BuildConfig.VERSION_CODE && it.downloadUrl.startsWith("https://") }
            }
        }.getOrNull()
    }
}
