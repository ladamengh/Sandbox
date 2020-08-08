package com.example.sandboxlog

import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class LogManager(private val filesDirectory: File) {

    companion object {
        @JvmField
        val TAG = LogManager::class.java.simpleName
    }

    lateinit var loggingProcess: Process
    lateinit var logFile: File

    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH-mm", Locale.getDefault())

    fun createLogFile() {
        Log.d(TAG, "Creating new log file")

        val currentTime = formatter.format(Calendar.getInstance().time)
        val fileName = "logs-$currentTime.log"

        filesDirectory.mkdirs()

        logFile = File(filesDirectory, fileName)

        if (!logFile.exists()) {
            logFile.createNewFile()
            Log.d(TAG, "LogFile $logFile created")
        } else {
            Log.d(TAG, "Log file already exists")
        }
    }

    fun getFilePath(): String { return logFile.absolutePath }

    fun startLogging() {
        Log.d(TAG, "Logging started")

        loggingProcess = Runtime.getRuntime().exec("logcat -f $logFile")
    }

    fun resumeLogging() {
        Log.d(TAG, "Logging resumed")

        loggingProcess.destroy()
        loggingProcess.waitFor()
        loggingProcess = Runtime.getRuntime().exec("logcat -f $logFile")
    }

    fun pauseLogging() {
        Log.d(TAG, "Logging paused")

        loggingProcess.destroy()
        loggingProcess.waitFor()
    }

    fun stopLogging() {
        Log.d(TAG, "Logging stopped")

        loggingProcess.destroy()
        loggingProcess.waitFor()
        loggingProcess = Runtime.getRuntime().exec("logcat -b all -c")
        loggingProcess.waitFor()
        loggingProcess.destroy()
        loggingProcess.waitFor()
    }
}