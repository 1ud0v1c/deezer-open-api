package com.ludovic.vimont.deezeropenapi.player

/**
 * Used to know, if the click come from the UI (for example in an item of the adapter) or from
 * the media session notification
 */
enum class ActionFrom {
    UI,
    NOTIFICATION
}