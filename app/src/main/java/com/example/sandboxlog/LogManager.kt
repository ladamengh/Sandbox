package com.example.sandboxlog

import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class LogManager(private val filesDirectory: File) {

    companion object {
        @JvmField
        val TAG = LogManager::class.java.simpleName
    }

    private lateinit var loggingProcess: Process
    private var logFile: File? = null

    var actualFilePath: String? = null
        private set

    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss.SSS", Locale.getDefault())

    private fun createLogFile() {
        Log.d(TAG, "Creating new log file")

        if (!filesDirectory.exists()) throw FileNotFoundException("File directory doesn't exist")

        val currentTime = formatter.format(Calendar.getInstance().time)
        val fileName = "logs-$currentTime.log"

        logFile = File(filesDirectory, fileName).also {
            it.createNewFile()
            actualFilePath = it.absolutePath
        }

        Log.d(TAG, "LogFile $logFile created")
    }

    fun resumeLogging() {
        Log.d(TAG, "Logging resumed")

        if (logFile == null) {
            createLogFile()
        }

        loggingProcess = Runtime.getRuntime().exec("logcat -f $logFile")
    }

    fun pauseLogging() {
        Log.d(TAG, "Logging paused")

        loggingProcess.finish()
    }

    fun stopLogging() {
        Log.d(TAG, "Logging stopped")

        logFile = null
        Log.d(TAG, "LOGFILE IS NULL")

        loggingProcess.finish()
        loggingProcess = Runtime.getRuntime().exec("logcat -b all -c")
        loggingProcess.waitFor()
        loggingProcess.finish()
    }

    private fun Process.finish() {
        try {
            exitValue()
        } catch (e: IllegalThreadStateException) {
            destroy()
            waitFor()
        }
    }
}