package com.example.sandboxlog.interactor

import com.example.sandboxlog.repository.LogRepository

class StopLogging(private val logRepository: LogRepository) {

    suspend operator fun invoke() = logRepository.stopLogging()
}
