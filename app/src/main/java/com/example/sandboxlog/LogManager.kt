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
    private var logFile: File? = null
    private var actualFilePath: String? = null

    private val formatter = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss.SSS", Locale.getDefault())

    private fun Process.finish() {
        this.destroy()
        this.waitFor()
    }

    private fun createLogFile() {
        Log.d(TAG, "Creating new log file")

        val currentTime = formatter.format(Calendar.getInstance().time)
        val fileName = "logs-$currentTime.log"

        filesDirectory.mkdirs()
        logFile = File(filesDirectory, fileName)
        actualFilePath = logFile!!.absolutePath

        Log.d(TAG, "LogFile $logFile created")
    }

    fun getFilePath(): String? { return actualFilePath }

    fun resumeLogging() {
        Log.d(TAG, "Logging resumed")

        if (actualFilePath == null || logFile == null) createLogFile()

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
}