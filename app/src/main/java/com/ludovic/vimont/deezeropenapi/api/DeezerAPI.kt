package com.ludovic.vimont.deezeropenapi.api

import com.ludovic.vimont.deezeropenapi.model.AlbumResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerAPI {
    object Constants {
        const val BASE_URL = "https://api.deezer.com/2.0/"
        const val NUMBER_OF_ITEM_PER_REQUEST = 25
    }

    @GET("user/2529/albums")
    fun getAlbums(@Query("index") index: Int): Call<AlbumResponse>
}