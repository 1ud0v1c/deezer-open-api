package com.ludovic.vimont.deezeropenapi.model

data class Track(
    val id: Int,
    val readable: Boolean,
    val isrc: String,
    val title: String,
    val title_short: String,
    val link: String,
    val preview: String,
    val duration: Int,
    val disk_number: Int,
    val rank: Int,
    val explicit_lyrics: Boolean,
    val explicit_content_cover: Int,
    val explicit_content_lyrics: Int,
    val track_position: Int
)