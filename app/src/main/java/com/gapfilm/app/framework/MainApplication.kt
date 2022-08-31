package com.gapfilm.app.framework

import android.app.Application
import com.gapfilm.app.BuildConfig
import io.adtrace.sdk.AdTrace
import io.adtrace.sdk.AdTraceConfig
import io.adtrace.sdk.LogLevel

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AdTrace.onCreate(
            AdTraceConfig(
                this,
                BuildConfig.API_KEY,
                if (BuildConfig.DEBUG) AdTraceConfig.ENVIRONMENT_SANDBOX else AdTraceConfig.ENVIRONMENT_PRODUCTION
            ).apply {
                if (BuildConfig.DEBUG) setLogLevel(LogLevel.VERBOSE)
            }
        )

        registerActivityLifecycleCallbacks(AdTraceLifecycleCallbacks())
    }
}