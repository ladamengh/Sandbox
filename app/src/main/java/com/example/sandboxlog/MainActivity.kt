package com.example.sandboxlog

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

open class MainActivity : AppCompatActivity(), MyCallbacks {

    companion object {
        @JvmField
        val TAG = MainActivity::class.java.simpleName
    }

    private val workManager = WorkManager.getInstance(application)

    private lateinit var loggingProcess: Process
    private var exceptionFile: File? = null
    private lateinit var logFile: File
    private var isLogging = false

    private val formatter =
        SimpleDateFormat("dd-MM-yyyy_HH-mm", Locale
            .getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "MainActivity created")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(this)
        }

        Thread.setDefaultUncaughtExceptionHandler(CrashHandler())
        createLogFile()
        startLogging()

        button.setOnClickListener {
            // creating an exception
            RequestBody.create(MultipartBody.FORM, exceptionFile!!)
        }

        buttonSend.setOnClickListener {
            uploadLogs()
        }

        buttonSecondActivity.setOnClickListener {
            Log.d(TAG, "Starting second activity")
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createLogFile() {
        val currentTime = formatter.format(Calendar.getInstance().time)
        val fileName = "logs-$currentTime.log"

        val fileDirectory = File(filesDir.absolutePath + File.separator + "sandboxLog")
        fileDirectory.mkdirs()
        Log.d(TAG, "FileDir exists? $fileDirectory, ${fileDirectory.exists()}")

        logFile = File(fileDirectory, fileName)
        logFile.createNewFile()

        Log.d(TAG, "File exists? $logFile, ${logFile.exists()}")
    }

    private fun startLogging() {
        isLogging = true
        loggingProcess = Runtime.getRuntime().exec("logcat -f $logFile")
    }

    private fun stopLogging() {
        isLogging = false
        loggingProcess.destroy()
    }

    private fun uploadLogs() {
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

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity resumed")
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