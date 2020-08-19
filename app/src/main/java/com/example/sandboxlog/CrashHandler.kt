package com.example.sandboxlog

import android.util.Log
import com.example.sandboxlog.interactor.CreateUploadLogsTask
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

class CrashHandler(private val createUploadLogsTask: CreateUploadLogsTask): Thread.UncaughtExceptionHandler {

    companion object {
        @JvmField
        val TAG = CrashHandler::class.java.simpleName
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        Log.e(TAG, "uncaught exception $e")

        runBlocking { createUploadLogsTask() }

        exitProcess(1)
    }
}