package com.example.sandboxlog

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.io.File

class SandboxApp: Application(), MyCallbacks {

    companion object {
        @JvmField
        val TAG = SandboxApp::class.java.simpleName
    }

    private lateinit var workManager: WorkManager
    private lateinit var fileDir: File
    private lateinit var logManager: LogManager

    override fun onCreate() {
        super.onCreate()

        workManager = WorkManager.getInstance(applicationContext)
        fileDir = File(filesDir.absolutePath + File.separator + "sandboxLog")
        logManager = LogManager(fileDir)

        Thread.setDefaultUncaughtExceptionHandler(CrashHandler { enqueueRequest() })

        registerActivityLifecycleCallbacks(this)

        logManager.createLogFile()
    }

    fun uploadLogs() {
        Log.d(TAG, "Uploading log files on request")

        enqueueRequest()

        logManager.createLogFile()
        logManager.resumeLogging()
    }

    private fun enqueueRequest() {
        logManager.stopLogging()

        val data = Data.Builder()
            .putString("file path", logManager.getFilePath())
            .build()

        val request = OneTimeWorkRequest.Builder(LogsWorker::class.java)
            .setInputData(data)
            .build()

        workManager.enqueue(request)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityCreated(activity, savedInstanceState)

        Log.d(TAG, "${activity.localClassName} onActivityCreated method")
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)

        Log.d(TAG, "${activity.localClassName} onActivityStarted method")
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)

        logManager.resumeLogging()
        Log.d(TAG, "${activity.localClassName} onActivityResumed method")
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)

        logManager.pauseLogging()
        Log.d(TAG, "${activity.localClassName} onActivityPaused method")
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)

        Log.d(TAG, "${activity.localClassName} onActivityStopped method")
    }
}