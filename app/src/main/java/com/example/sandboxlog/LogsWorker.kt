package com.example.sandboxlog

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.impl.Schedulers
import com.example.sandboxlog.service.RetrofitClient
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers.io
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class LogsWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {

    companion object {
        @JvmField
        val TAG = LogsWorker::class.java.simpleName

        private var FILE_PATH = "file path"
    }

    private val apiService = RetrofitClient.create()
    private lateinit var filePathData: String

    override fun doWork(): Result {
        Log.d(TAG,"Loading logs to the server")

        filePathData = inputData.getString(FILE_PATH).toString()

        if (filePathData.isNullOrEmpty()) {
            return Result.failure()
        }

        uploadLogs(filePathData)
        return Result.success()
    }

    private fun uploadLogs(filePath: String): Result {
        return try {
            val logFile = File(filePath)

            val filePart = RequestBody.create(MultipartBody.FORM, logFile)
            val file = MultipartBody.Part.createFormData(
                logFile.name,
                logFile.name,
                filePart
            )

            val result = apiService.uploadLogs(file).execute()

            if (result.isSuccessful) Result.success() else Result.failure()
        } catch (throwable: Throwable) {
             Log.e(TAG, "An error occurred: $throwable")
             Result.failure()
         }
    }
}