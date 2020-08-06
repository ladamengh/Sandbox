package com.example.sandboxlog

import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class CrashHandler(
    val stopLogging: () -> Unit,
    val getFilePath: () -> String
): Thread.UncaughtExceptionHandler {

    companion object {
        @JvmField
        val TAG = CrashHandler::class.java.simpleName
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.e(TAG, "uncaught exception $e")

        stopLogging()

        val data = Data.Builder()
            .putString("file path", getFilePath())
            .build()

        val request = OneTimeWorkRequest.Builder(LogsWorker::class.java)
            .setInputData(data)
            .build()

        WorkManager.getInstance().enqueue(request)

        TimeUnit.SECONDS.sleep(1)
        exitProcess(1)
    }
}