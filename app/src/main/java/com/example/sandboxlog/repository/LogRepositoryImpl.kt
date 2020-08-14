package com.example.sandboxlog.repository

import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.sandboxlog.LogManager
import com.example.sandboxlog.LogsWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.invoke

class LogRepositoryImpl(private val logManager: LogManager): LogRepository {
    override fun startLogging() {
        logManager.resumeLogging()
    }

    @ExperimentalCoroutinesApi
    override suspend fun pauseLogging() {
        Dispatchers.IO {
            logManager.pauseLogging()
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun stopLogging() {
        Dispatchers.IO {
            logManager.stopLogging()
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun uploadLogs() {
        Dispatchers.IO {
            stopLogging()

            val data = Data.Builder()
                .putString("file path", logManager.getFilePath())
                .build()

            val request = OneTimeWorkRequest.Builder(LogsWorker::class.java)
                .setInputData(data)
                .build()

            Log.d("LogRepositoryImpl", "Trying to enqueue request")

            WorkManager.getInstance().enqueue(request) // ???without context
        }
    }
}