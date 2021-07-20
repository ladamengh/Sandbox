package com.example.sandboxlog

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.sandboxlog.interactor.UploadLogs
import com.example.sandboxlog.service.RetrofitClient
import kotlinx.coroutines.runBlocking

class LogsWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    companion object {
        @JvmField
        val TAG = LogsWorker::class.java.simpleName

        const val LOG_FILE_PATH = "log_file_path"
    }

    lateinit var uploadLogs: UploadLogs

    override fun doWork(): Result {
        Log.d(TAG, "Loading logs to the server")

        val filePathData = inputData.getString(LOG_FILE_PATH) ?: return Result.failure()

        return if (filePathData.isBlank()) {
            Result.failure()
        } else {
            val result = runBlocking { uploadLogs(filePathData) }
            if (result is RetrofitClient.Result.Success) Result.success() else Result.failure()
        }
    }
}
