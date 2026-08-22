package com.example

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.example.telemetry.QiraatiTelemetry

class QiraatiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AppContextHolder.init(this)
        FirebaseApp.initializeApp(this)
        FirebaseAnalytics.getInstance(this).logEvent("qiraati_app_initialized", null)
        FirebaseMessaging.getInstance().subscribeToTopic(QiraatiFirebase.TOPIC_UPDATES)

        // Initializes AdMob with the production App ID declared in AndroidManifest.xml.
        MobileAds.initialize(this)
        QiraatiTelemetry.record(this, "app_open")
        QiraatiTelemetry.record(this, "session_start")
    }
}

object QiraatiFirebase {
    const val TOPIC_UPDATES = "qiraati-updates"

    fun logScreen(screenName: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ComposeScreen")
        }
        FirebaseAnalytics.getInstance(AppContextHolder.application)
            .logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

    fun logEvent(name: String, parameters: Map<String, String> = emptyMap()) {
        val bundle = Bundle().apply {
            parameters.forEach { (key, value) -> putString(key, value) }
        }
        FirebaseAnalytics.getInstance(AppContextHolder.application).logEvent(name, bundle)
    }
}

internal object AppContextHolder {
    lateinit var application: Application
        private set

    fun init(application: Application) {
        this.application = application
    }
}
