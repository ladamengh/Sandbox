package com.example.sandboxlog

import android.app.Application
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.sandboxlog.interactor.PauseLogging
import com.example.sandboxlog.interactor.StartLogging
import com.example.sandboxlog.interactor.StopLogging
import com.example.sandboxlog.interactor.UploadLogs
import com.example.sandboxlog.repository.LogRepositoryImpl
import java.io.File

class SandboxApp: Application(){

    companion object {
        @JvmField
        val TAG = SandboxApp::class.java.simpleName
    }

    lateinit var startLogging: StartLogging
    lateinit var pauseLogging: PauseLogging
    lateinit var uploadLogs: UploadLogs

    private lateinit var workManager: WorkManager
    private lateinit var fileDir: File
    private lateinit var logManager: LogManager
    private lateinit var logRepositoryImpl: LogRepositoryImpl

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate method called")

        workManager = WorkManager.getInstance(applicationContext)
        fileDir = File(filesDir.absolutePath + File.separator + "sandboxLog")
        logManager = LogManager(fileDir)

        logRepositoryImpl = LogRepositoryImpl(logManager)
        startLogging = StartLogging(logRepositoryImpl)
        pauseLogging = PauseLogging(logRepositoryImpl)
        uploadLogs = UploadLogs(logRepositoryImpl)

        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(uploadLogs))

        registerActivityLifecycleCallbacks(ActivityLifecycleLogManager(startLogging, pauseLogging))
    }
}