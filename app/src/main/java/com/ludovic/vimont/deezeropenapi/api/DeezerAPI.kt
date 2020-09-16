package com.ludovic.vimont.deezeropenapi.api

import com.ludovic.vimont.deezeropenapi.model.AlbumResponse
import com.ludovic.vimont.deezeropenapi.model.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Represent the Deezer OpenAPI, We can find two endpoints:
 * <ul>
 *     <li>getAlbums: get information about a user albums</li>
 *     <li>getTracks: get information about the tracks of an albums, we need the album_id</li>
 * </ul>
 */
interface DeezerAPI {
    object Constants {
        const val HOSTNAME = "api.deezer.com"
        const val CDN_HOSTNAME = "cdns-preview-2.dzcdn.net"
        const val BASE_URL = "https://api.deezer.com/2.0/"
        const val NUMBER_OF_ITEM_PER_REQUEST = 25
    }

    @GET("user/2529/albums")
    fun getAlbums(@Query("index") index: Int): Call<AlbumResponse>

    @GET("album/{album_id}/tracks")
    fun getTracks(@Path("album_id") albumId: Int): Call<TrackResponse>
}