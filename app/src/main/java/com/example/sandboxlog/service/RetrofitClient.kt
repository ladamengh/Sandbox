package com.example.sandboxlog.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    fun create(): GateDataService {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client =
            OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://gate-test-data.ecp-share.com")
            .client(client)
            .build()

        return retrofit.create(GateDataService::class.java);
    }

    suspend inline fun <T : Any> safeApiCall(crossinline apiRequest: suspend () -> T): Result<T> =
        try {
            Result.Success(apiRequest())
        } catch (e: Exception) {
            Result.Error(e)
        }

    sealed class Result<out T : Any> {

        data class Success<out Type : Any>(val data: Type) : Result<Type>()
        data class Error(val exception: Exception) : Result<Nothing>()
    }
}
