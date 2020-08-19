package com.example.sandboxlog.repository

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.sandboxlog.LogManager
import com.example.sandboxlog.LogsWorker
import com.example.sandboxlog.LogsWorker.Companion.LOG_FILE_PATH
import com.example.sandboxlog.service.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.example.sandboxlog.service.RetrofitClient.Result
import java.io.File

class LogRepositoryImpl(
    private val logManager: LogManager,
    private val appCtx: Context
): LogRepository {

    private val apiService = RetrofitClient.create()

    override fun startLogging() {
        logManager.resumeLogging()
    }

    override suspend fun pauseLogging() {
        withContext(Dispatchers.IO) {
            logManager.pauseLogging()
        }
    }

    override suspend fun stopLogging() {
        withContext(Dispatchers.IO) {
            logManager.stopLogging()
        }
    }

    override suspend fun createUploadLogsTask() {
        withContext(Dispatchers.IO) {
            stopLogging()

            val data = Data.Builder()
                .putString(LOG_FILE_PATH, logManager.actualFilePath)
                .build()

            val request = OneTimeWorkRequest.Builder(LogsWorker::class.java)
                .setInputData(data)
                .build()

            Log.d("LogRepositoryImpl", "Trying to enqueue request, appCtx is $appCtx")

            WorkManager.getInstance(appCtx).enqueue(request)
        }
    }

    override suspend fun uploadLogs(logFilePath: String): Result<Any> {
        val file = File(logFilePath).let {
            MultipartBody.Part.createFormData(
                it.name,
                it.name,
                it.asRequestBody(MultipartBody.FORM)
            )
        }

        return try {
            Log.d("LogRepositoryImpl", "Really uploading logs by apiService")
            val result = RetrofitClient.safeApiCall {
                apiService.uploadLogs(file)
            }
            Log.d("LogRepositoryImpl", "The result is $result")
            result
        } catch (throwable: Throwable) {
            Log.e(LogsWorker.TAG, "An error occurred: $throwable")
            Result.Error(throwable as Exception)
        }
    }
}