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
import kotlinx.coroutines.withContext

class LogRepositoryImpl(
    private val logManager: LogManager,
    private val workManager: WorkManager
): LogRepository {

    companion object {
        private var FILE_PATH = "file path"
    }

    override fun startLogging() {
        logManager.resumeLogging()
    }

    @ExperimentalCoroutinesApi
    override suspend fun pauseLogging() {
        withContext(Dispatchers.IO) {
            logManager.pauseLogging()
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun stopLogging() {
        withContext(Dispatchers.IO) {
            logManager.stopLogging()
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun uploadLogs() {
        withContext(Dispatchers.IO) {
            stopLogging()

            val data = Data.Builder()
                .putString(FILE_PATH, logManager.actualFilePath)
                .build()

            val request = OneTimeWorkRequest.Builder(LogsWorker::class.java)
                .setInputData(data)
                .build()

            Log.d("LogRepositoryImpl", "Trying to enqueue request")

            workManager.enqueue(request)

            startLogging()
        }
    }
}