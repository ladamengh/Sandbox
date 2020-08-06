package com.example.sandboxlog

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class SandboxApp: Application(), MyCallbacks {

    companion object {
        @JvmField
        val TAG = SandboxApp::class.java.simpleName
    }

    private val logManager = LogManager()
    private lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()

        workManager = WorkManager.getInstance(applicationContext)

        logManager.setCrashHandler()
        logManager.setWorkManager(workManager)

        registerActivityLifecycleCallbacks(this)

        val fileDirectory = File(filesDir.absolutePath + File.separator + "sandboxLog")
        logManager.createLogFile(fileDirectory)
    }

    fun upload() {
        logManager.uploadLogs()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityCreated(activity, savedInstanceState)

        //isLogging = true
        Log.d(TAG, "${activity.localClassName} onActivityCreated method")
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)

        //isLogging = true
        Log.d(TAG, "${activity.localClassName} onActivityStarted method")
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)

        //isLogging = true
        logManager.startLogging()
        Log.d(TAG, "${activity.localClassName} onActivityResumed method")
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)

        //isLogging = false
        logManager.stopLogging()
        Log.d(TAG, "${activity.localClassName} onActivityPaused method")
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)

        Log.d(TAG, "${activity.localClassName} onActivityStopped method")
    }
}