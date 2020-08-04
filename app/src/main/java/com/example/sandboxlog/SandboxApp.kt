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
    }

    fun createLogFile() {
        val currentTime = formatter.format(Calendar.getInstance().time)
        val fileName = "logs-$currentTime.log"

        fileDirectory = File(filesDir.absolutePath + File.separator + "sandboxLog")
        fileDirectory.mkdirs()
        Log.d(TAG, "FileDir exists? $fileDirectory, ${fileDirectory.exists()}")

        logFile = File(fileDirectory, fileName)
        logFile.createNewFile()

        Log.d(TAG, "File exists? $logFile, ${logFile.exists()}")
    }

    fun startLogging() {
        isLogging = true
        loggingProcess = Runtime.getRuntime().exec("logcat -f $logFile")
    }

    private fun stopLogging() {
        isLogging = false
        loggingProcess.destroy()
    }

    fun uploadLogs() {
        stopLogging()

        val data = Data.Builder()
            .putString("file path", logFile.absolutePath)
            .build()

        val request = OneTimeWorkRequest.Builder(LogsWorker::class.java)
            .setInputData(data)
            .build()

        workManager.enqueue(request)

        createLogFile()
        startLogging()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityCreated(activity, savedInstanceState)

        Log.d(TAG, "onActivityCreated method")
        createLogFile()
        startLogging()
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)

        Log.d(TAG, "onActivityStarted method")
        startLogging()
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)

        Log.d(TAG, "onActivityResumed method")
        startLogging()
    }

    override fun onActivityPaused(activity: Activity) {
        super.onActivityPaused(activity)

        Log.d(TAG, "onActivityPaused method")
        loggingProcess = Runtime.getRuntime().exec("logcat -c")
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)

        Log.d(TAG, "onActivityStopped method")
        loggingProcess = Runtime.getRuntime().exec("logcat -c")
    }
}