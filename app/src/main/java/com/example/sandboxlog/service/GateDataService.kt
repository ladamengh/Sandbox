package com.example.sandboxlog.service

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GateDataService {

    @Multipart
    @POST("/logs_ecpshare")
    suspend fun uploadLogs(@Part file: MultipartBody.Part): ResponseBody
}
