package com.example.sandboxlog.interactor

import com.example.sandboxlog.repository.LogRepository

class PauseLogging(private val logRepository: LogRepository) {

    suspend operator fun invoke() = logRepository.pauseLogging()
}
