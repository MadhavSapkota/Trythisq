package com.example.network

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface ApiService {
    @GET
    suspend fun fetchRawJson(@Url url: String): Response<ResponseBody>

    companion object {
        fun create(): ApiService {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://localhost/") // Mandatory base URL, overwritten by dynamic @Url
                .client(okHttpClient)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
