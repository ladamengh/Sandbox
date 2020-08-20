package com.example.sandboxlog.interactor

import com.example.sandboxlog.repository.LogRepository

class UploadLogs(private val logRepository: LogRepository) {

    suspend operator fun invoke(logFilePath: String) = logRepository.uploadLogs(logFilePath)
}