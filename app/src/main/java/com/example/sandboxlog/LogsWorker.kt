package com.example.sandboxlog

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.sandboxlog.repository.LogRepository
import com.example.sandboxlog.repository.LogRepositoryImpl
import com.example.sandboxlog.service.RetrofitClient
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class LogsWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {

    companion object {
        @JvmField
        val TAG = LogsWorker::class.java.simpleName

        const val LOG_FILE_PATH = "log_file_path"
    }

    lateinit var logRepository: LogRepository

    override fun doWork(): Result {
        Log.d(TAG,"Loading logs to the server")

        val filePathData = inputData.getString(LOG_FILE_PATH) ?: return Result.failure()

         return if (filePathData.isBlank()) Result.failure() else
            try {
                val result = runBlocking {
                    logRepository.uploadLogs(filePathData)
                }
                Log.d(TAG, "Enqueuing request with LogsWorker, result: $result")
                if (result is RetrofitClient.Result.Success) {
                    return Result.success()
                } else {
                    Result.failure()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to enqueue request with LogsWorker, error: $e")
                return Result.failure()
            }
    }
}