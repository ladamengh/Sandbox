package com.example.sandboxlog.interactor

import com.example.sandboxlog.repository.LogRepository

class CreateUploadLogsTask(private val logRepository: LogRepository) {

    suspend operator fun invoke() = logRepository.createUploadLogsTask()
}
