package com.example.sandboxlog.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun create(): GateDataService {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://gate-test-data.ecp-share.com")
            .build()

        return retrofit.create(GateDataService::class.java);
    }
}
