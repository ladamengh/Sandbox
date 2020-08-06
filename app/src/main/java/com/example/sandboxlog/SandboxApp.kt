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

    private val logManager = LogManager()
    private lateinit var workManager: WorkManager
    private lateinit var fileDir: File

    override fun onCreate() {
        super.onCreate()

        workManager = WorkManager.getInstance(applicationContext)

        Thread.setDefaultUncaughtExceptionHandler(
            CrashHandler(
                { logManager.stopLogging() },
                { logManager.getFilePath() }
            )
        )

        registerActivityLifecycleCallbacks(this)

        fileDir = File(filesDir.absolutePath + File.separator + "sandboxLog")
        logManager.createLogFile(fileDir)
    }

    fun uploadLogs() {
        Log.d(TAG, "Uploading log files on request")

        val data = Data.Builder()
            .putString("file path", logManager.getFilePath())
            .build()

        val request = OneTimeWorkRequest.Builder(LogsWorker::class.java)
            .setInputData(data)
            .build()

        //thread {
        logManager.stopLogging()

        workManager.enqueue(request)

        logManager.createLogFile(fileDir)
        logManager.resumeLogging()
        //}
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

        logManager.loggingProcess = Runtime.getRuntime().exec("logcat -f ${logManager.logFile}")
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