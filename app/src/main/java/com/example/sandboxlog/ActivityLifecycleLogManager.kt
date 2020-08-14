package com.example.sandboxlog

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.sandboxlog.interactor.PauseLogging
import com.example.sandboxlog.interactor.StartLogging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ActivityLifecycleLogManager(
    private val startLogging: StartLogging,
    private val pauseLogging: PauseLogging
): Application.ActivityLifecycleCallbacks {

    override fun onActivityResumed(activity: Activity) {
        startLogging.invoke()
    }

    override fun onActivityPaused(activity: Activity) {
        GlobalScope.launch { pauseLogging.invoke() }
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
}