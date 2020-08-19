package com.example.sandboxlog

import android.app.Application
import android.util.Log
import androidx.work.*
import com.example.sandboxlog.interactor.PauseLogging
import com.example.sandboxlog.interactor.StartLogging
import com.example.sandboxlog.interactor.CreateUploadLogsTask
import com.example.sandboxlog.repository.LogRepository
import com.example.sandboxlog.repository.LogRepositoryImpl
import java.io.File

class SandboxApp: Application(), Configuration.Provider {

    companion object {
        @JvmField
        val TAG = SandboxApp::class.java.simpleName
    }

    lateinit var startLogging: StartLogging
    lateinit var createUploadLogsTask: CreateUploadLogsTask
    private lateinit var pauseLogging: PauseLogging

    private lateinit var workManager: WorkManager
    private lateinit var fileDir: File
    private lateinit var logManager: LogManager
    private lateinit var logRepositoryImpl: LogRepository

    override fun getWorkManagerConfiguration(): Configuration {
        val myWorkerFactory = DelegatingWorkerFactory()
        myWorkerFactory.addFactory(MyWorkerFactory(logRepositoryImpl))

        return Configuration.Builder()
            .setWorkerFactory(myWorkerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate method called")

        fileDir = File(filesDir.absolutePath + File.separator + "sandboxLog")
        fileDir.mkdirs()
        logManager = LogManager(fileDir)

        logRepositoryImpl = LogRepositoryImpl(logManager, applicationContext)

        WorkManager.initialize(this, workManagerConfiguration)
        workManager = WorkManager.getInstance(applicationContext)

        startLogging = StartLogging(logRepositoryImpl)
        pauseLogging = PauseLogging(logRepositoryImpl)
        createUploadLogsTask = CreateUploadLogsTask(logRepositoryImpl)

        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(createUploadLogsTask))

        registerActivityLifecycleCallbacks(ActivityLifecycleLogManager(startLogging, pauseLogging))
    }
}