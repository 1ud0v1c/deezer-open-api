package com.ludovic.vimont.deezeropenapi.model

/**
 * Endpoint response of Deezer's Open API which give us the possibility to have all
 * the tracks of a specific album.
 * @see com.ludovic.vimont.deezeropenapi.api.DeezerAPI
 */
data class TrackResponse(
    val total: Int,
    val data: List<Track>
)