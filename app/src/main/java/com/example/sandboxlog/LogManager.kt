package com.example.sandboxlog

import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class LogManager() {

    companion object {
        @JvmField
        val TAG = LogManager::class.java.simpleName
    }

    private lateinit var loggingProcess: Process
    private lateinit var logFile: File
    private lateinit var fileDir: File
    private var isLogging = false
    private lateinit var manager: WorkManager

    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH-mm", Locale.getDefault())

    fun setCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this::uploadLogs))
    }

    fun setWorkManager(workManager: WorkManager) {
        manager = workManager
    }

    fun createLogFile(filesDirectory: File) {
        Log.d(TAG, "Creating new log file")

        val currentTime = formatter.format(Calendar.getInstance().time)
        val fileName = "logs-$currentTime.log"

        fileDir = filesDirectory
        fileDir.mkdirs()

        logFile = File(fileDir, fileName)

        if (!logFile.exists()) {
            logFile.createNewFile()
            Log.d(TAG, "LogFile $logFile created")
        } else {
            Log.d(TAG, "Log file already exists")
        }
    }

    fun startLogging() {
        Log.d(TAG, "Logging started")

        isLogging = true
        loggingProcess = Runtime.getRuntime().exec("logcat -f $logFile")
    }

    fun stopLogging() {
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

        thread {
            loggingProcess.destroy()
            loggingProcess.waitFor()
            loggingProcess = Runtime.getRuntime().exec("logcat -b all -c")
            loggingProcess.waitFor()
            loggingProcess.destroy()
            loggingProcess.waitFor()

            manager.enqueue(request)

            createLogFile(fileDir)

            loggingProcess = Runtime.getRuntime().exec("logcat -f $logFile")
        }
    }
}