package com.example.sandboxlog

import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.sandboxlog.interactor.UploadLogs
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class CrashHandler(private val uploadLogs: UploadLogs): Thread.UncaughtExceptionHandler {

    companion object {
        @JvmField
        val TAG = CrashHandler::class.java.simpleName
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.e(TAG, "uncaught exception $e")

        runBlocking { uploadLogs() }

        exitProcess(1)
    }
}