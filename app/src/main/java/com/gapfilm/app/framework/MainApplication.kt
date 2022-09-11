package com.gapfilm.app.framework

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.gapfilm.app.BuildConfig
import com.najva.sdk.NajvaClient
import com.najva.sdk.NajvaConfiguration
import io.adtrace.sdk.AdTrace
import io.adtrace.sdk.AdTraceConfig
import io.adtrace.sdk.LogLevel

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        setupAdtrace()
        setupNajva()
    }

    private fun setupAdtrace() {
        AdTrace.onCreate(
            AdTraceConfig(
                this,
                BuildConfig.ADTRACE_API_KEY,
                if (BuildConfig.DEBUG) AdTraceConfig.ENVIRONMENT_SANDBOX else AdTraceConfig.ENVIRONMENT_PRODUCTION
            ).apply {
                if (BuildConfig.DEBUG) setLogLevel(LogLevel.VERBOSE)
            }
        )

        registerActivityLifecycleCallbacks(AdTraceLifecycleCallbacks())
    }

    private fun setupNajva() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("news", "News", NotificationCompat.PRIORITY_HIGH)
            createNotificationChannel("updates", "Updates", NotificationCompat.PRIORITY_DEFAULT)
            NajvaClient.configuration.lowPriorityChannel = "news"
            NajvaClient.configuration.highPriorityChannel = "updates"
        }

        registerActivityLifecycleCallbacks(NajvaClient.getInstance(this, NajvaConfiguration()))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(id: String, name: String, priority: Int) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(
            NotificationChannel(id, name, priority).apply {
                enableLights(true)
                enableVibration(true)
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE), null)
                setShowBadge(true)
                setBypassDnd(true)
            }
        )
    }
}