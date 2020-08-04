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
import kotlin.system.exitProcess

class SandboxApp: Application(), MyCallbacks {

    companion object {
        @JvmField
        val TAG = SandboxApp::class.java.simpleName
    }

    private lateinit var loggingProcess: Process
    private lateinit var logFile: File
    private lateinit var fileDirectory: File
    private var isLogging = false

    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH-mm", Locale.getDefault())

    private lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()

        workManager = WorkManager.getInstance(this)
        registerActivityLifecycleCallbacks(this)
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this::uploadLogs))

        createLogFile()
    }

    private fun createLogFile() {
        Log.d(TAG, "Creating new log file")

        val currentTime = formatter.format(Calendar.getInstance().time)
        val fileName = "logs-$currentTime.log"

        fileDirectory = File(filesDir.absolutePath + File.separator + "sandboxLog")
        fileDirectory.mkdirs()

        logFile = File(fileDirectory, fileName)

        if (!logFile.exists()) {
            logFile.createNewFile()
            Log.d(TAG, "LogFile $logFile created")
        } else {
            Log.d(TAG, "Log file already exists")
        }

        startLogging()
    }

    private fun startLogging() {
        Log.d(TAG, "Logging started")

        isLogging = true
        loggingProcess = Runtime.getRuntime().exec("logcat -f $logFile")
    }

    private fun stopLogging() {
        Log.d(TAG, "Logging stopped")

        isLogging = false
        loggingProcess = Runtime.getRuntime().exec("logcat -b all -c")
        loggingProcess.destroy()
    }

    fun uploadLogs() {
        Log.d(TAG, "Uploading log files on request")

        val data = Data.Builder()
            .putString("file path", logFile.absolutePath)
            .build()

        val request = OneTimeWorkRequest.Builder(LogsWorker::class.java)
            .setInputData(data)
            .build()

        workManager.enqueue(request)

        createLogFile()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityCreated(activity, savedInstanceState)

        isLogging = true
        Log.d(TAG, "${activity.localClassName} onActivityCreated method")
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)

        isLogging = true
        Log.d(TAG, "${activity.localClassName} onActivityStarted method")
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)

        isLogging = true
        Log.d(TAG, "${activity.localClassName} onActivityResumed method")
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)

        isLogging = false
        Log.d(TAG, "${activity.localClassName} onActivityPaused method")
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)

        isLogging = false
        stopLogging()
        Log.d(TAG, "${activity.localClassName} onActivityStopped method")
    }
}