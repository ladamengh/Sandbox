package com.example.sandboxlog.repository

interface LogRepository {

    fun startLogging()

    suspend fun pauseLogging()

    suspend fun stopLogging()

    suspend fun uploadLogs()
}