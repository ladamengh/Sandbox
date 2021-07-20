package com.example.sandboxlog

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.sandboxlog.interactor.UploadLogs
import com.example.sandboxlog.repository.LogRepository

class MyWorkerFactory(private val logRepositoryImpl: LogRepository) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {

        return if (workerClassName == LogsWorker::class.java.name) {
            LogsWorker(appContext, workerParameters).also { it.uploadLogs = UploadLogs(logRepositoryImpl) }
        } else {
            null
        }
    }
}
