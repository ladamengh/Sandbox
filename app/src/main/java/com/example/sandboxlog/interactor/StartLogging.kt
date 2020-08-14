package com.example.sandboxlog.interactor

import com.example.sandboxlog.repository.LogRepository

class StartLogging(private val logRepository: LogRepository) {

    operator fun invoke() = logRepository.startLogging()
}