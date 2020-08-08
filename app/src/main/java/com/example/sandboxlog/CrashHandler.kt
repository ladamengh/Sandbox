package com.example.sandboxlog

import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class CrashHandler(val uploadCrashLogs: () -> Unit): Thread.UncaughtExceptionHandler {

    companion object {
        @JvmField
        val TAG = CrashHandler::class.java.simpleName
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.e(TAG, "uncaught exception $e")

        uploadCrashLogs()

        TimeUnit.SECONDS.sleep(1)
        exitProcess(1)
    }
}