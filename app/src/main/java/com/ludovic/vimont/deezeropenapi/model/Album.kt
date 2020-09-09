package com.ludovic.vimont.deezeropenapi.model

data class Album(
    val id: Int,
    val title: String,
    val link: String,
    val cover: String,
    val cover_small: String,
    val cover_medium: String,
    val cover_big: String,
    val cover_xl: String,
    val nb_tracks: Int,
    val release_date: String,
    val record_type: String,
    val alternative: Album? = null,
    val tracklist: String,
    val artist: Artist,
    val explicit_lyrics: Boolean,
    val time_add: Int
)