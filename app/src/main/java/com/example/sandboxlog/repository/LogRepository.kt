package com.example.sandboxlog.repository

import com.example.sandboxlog.service.RetrofitClient.Result

interface LogRepository {

    fun startLogging()

    suspend fun pauseLogging()

    suspend fun stopLogging()

    suspend fun createUploadLogsTask()

    suspend fun uploadLogs(logFilePath: String): Result<Any>
}
