package com.example.sandboxlog

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.sandboxlog.service.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class LogsWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {

    companion object {
        @JvmField
        val TAG = LogsWorker::class.java.simpleName

        const val LOG_FILE_PATH = "log_file_path"
    }

    private val apiService = RetrofitClient.create()
    private lateinit var filePathData: String

    override fun doWork(): Result {
        Log.d(TAG,"Loading logs to the server")

        filePathData = inputData.getString(LOG_FILE_PATH) ?: return Result.failure()

        return if (filePathData.isBlank()) Result.failure() else uploadLogs(filePathData)
    }

    private fun uploadLogs(filePath: String): Result {
        val file = File(filePath).let {
            MultipartBody.Part.createFormData(
                it.name,
                it.name,
                it.asRequestBody(MultipartBody.FORM)
            )
        }

        return try {
            val result = apiService.uploadLogs(file).execute()

            if (result.isSuccessful) Result.success() else Result.failure()
        } catch (throwable: Throwable) {
             Log.e(TAG, "An error occurred: $throwable")
             Result.failure()
         }
    }
}