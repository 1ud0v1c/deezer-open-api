package com.ludovic.vimont.deezeropenapi.model

/**
 * Endpoint response of Deezer's OpenAPI.
 * Which give us the possibility to list all the albums of an user.
 * Each album contains the corresponding artist & tracks.
 * @see com.ludovic.vimont.deezeropenapi.api.DeezerAPI
 */
data class AlbumResponse(
    val next: String?,
    val total: Int,
    val data: List<Album>,
    val checksum: String
)