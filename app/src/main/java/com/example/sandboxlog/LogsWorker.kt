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
    }

    private val apiService = RetrofitClient.create()
    private lateinit var filePathData: String

    override fun doWork(): Result {
        Log.d(TAG,"Loading logs to the server")

        return try {
            filePathData = inputData.getString("file path").toString()
            uploadLogs(filePathData)
            Result.success()
        } catch (throwable: Throwable) {
            Log.e(TAG,"Error $throwable")
            Result.failure()
        }
    }

    private fun uploadLogs(filePath: String) {
        val logFile = File(filePath)

        val filePart = RequestBody.create(MultipartBody.FORM, logFile)
        val file = MultipartBody.Part.createFormData(
            logFile.name,
            logFile.name,
            filePart
        )

        apiService.uploadLogs(file)
    }
}