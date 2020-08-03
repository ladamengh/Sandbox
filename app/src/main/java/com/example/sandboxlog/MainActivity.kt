package com.example.sandboxlog

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val TAG = MainActivity::class.java.simpleName
    }

    private val workManager = WorkManager.getInstance(application)

    private lateinit var loggingProcess: Process
    private lateinit var logFile: File
    private var isLogging = false

    private val formatter =
        SimpleDateFormat("dd-MM-yyyy_HH-mm", Locale.getDefault())

    private fun createLogFile() {
        val currentTime = formatter.format(Calendar.getInstance().time)
        val fileName = "logs-$currentTime.log"

        val fileDirectory = File(filesDir.absolutePath + File.separator + "sandboxLog")
        fileDirectory.mkdirs()
        //Log.d(TAG, "FileDir exists? $fileDirectory, ${fileDirectory.exists()}")

        logFile = File(fileDirectory, fileName)
        logFile.createNewFile()

        if (logFile.exists()) {
            Toast.makeText(this, "LogFile created", Toast.LENGTH_SHORT).show()
        }
        //Log.d(TAG, "File exists? $logFile, ${logFile.exists()}")
    }

    private fun startLogging() {
        isLogging = true
        loggingProcess = Runtime.getRuntime().exec("logcat -f $logFile")
    }

    private fun stopLogging() {
        isLogging = false
        loggingProcess.destroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "MainActivity created")

        createLogFile()
        startLogging()

        button.setOnClickListener {
            Log.d(TAG, "Button pressed")
            stopLogging()
        }

        buttonSend.setOnClickListener {
            uploadLogs()
        }
    }

    private fun uploadLogs() {
        val data = Data.Builder()
            .putString("file path", logFile.absolutePath)
            .build()

        val request = OneTimeWorkRequest
            .Builder(LogsWorker::class.java)
            .setInputData(data)
            .build()

        workManager.enqueue(request)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity resumed")
    }
}