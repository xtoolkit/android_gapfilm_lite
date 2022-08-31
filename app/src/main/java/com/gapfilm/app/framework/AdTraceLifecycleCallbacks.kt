package com.gapfilm.app.framework

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.adtrace.sdk.AdTrace

class AdTraceLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityResumed(p0: Activity) = AdTrace.onResume()

    override fun onActivityPaused(p0: Activity) = AdTrace.onPause()

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {}
}