package com.example.sandboxlog

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.impl.Schedulers
import com.example.sandboxlog.repository.LogRepositoryImpl.Companion.FILE_PATH
import com.example.sandboxlog.service.RetrofitClient
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers.io
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class LogsWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {

    companion object {
        @JvmField
        val TAG = LogsWorker::class.java.simpleName
    }

    private val apiService = RetrofitClient.create()
    private lateinit var filePathData: String

    override fun doWork(): Result {
        Log.d(TAG,"Loading logs to the server")

        filePathData = inputData.getString(FILE_PATH) ?: return Result.failure()

        if (filePathData.isBlank()) {
            return Result.failure()
        }

        return uploadLogs(filePathData)
    }

    private fun uploadLogs(filePath: String): Result {
        val logFile = File(filePath)

        val filePart = logFile.asRequestBody(MultipartBody.FORM)
        val file = MultipartBody.Part.createFormData(
            logFile.name,
            logFile.name,
            filePart
        )

        return try {
            val result = apiService.uploadLogs(file).execute()

            if (result.isSuccessful) Result.success() else Result.failure()
        } catch (throwable: Throwable) {
             Log.e(TAG, "An error occurred: $throwable")
             Result.failure()
         }
    }
}