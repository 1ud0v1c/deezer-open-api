package com.ludovic.vimont.deezeropenapi.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Used to make request to the albums endpoint of Deezer's OpenAPI using the retrofit library.
 * @see: https://square.github.io/retrofit/
 */
object DeezerService {
    private var api: DeezerAPI
    private var client: OkHttpClient = OkHttpBuilder.getClient()

    init {
        val retrofit: Retrofit = buildRetrofit()
        api = retrofit.create(DeezerAPI::class.java)
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DeezerAPI.Constants.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    fun getAPI(): DeezerAPI = api

    fun setClient(okHttpClient: OkHttpClient) {
        this.client = okHttpClient
        val retrofit: Retrofit = buildRetrofit()
        api = retrofit.create(DeezerAPI::class.java)
    }

    fun setClient(timeout: Long, timeUnit: TimeUnit) {
        val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(timeout, timeUnit)
            .readTimeout(timeout, timeUnit)
            .writeTimeout(timeout, timeUnit)
            .build()
        setClient(okHttpClient)
    }
}