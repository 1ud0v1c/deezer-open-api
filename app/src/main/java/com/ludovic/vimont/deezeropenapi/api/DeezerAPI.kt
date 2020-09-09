package com.ludovic.vimont.deezeropenapi.api

import com.ludovic.vimont.deezeropenapi.model.AlbumResponse
import retrofit2.Call
import retrofit2.http.GET

interface DeezerAPI {
    object Constants {
        const val BASE_URL = "https://api.deezer.com/2.0/"
    }

    @GET("user/2529/albums")
    fun getAlbums(): Call<AlbumResponse>
}