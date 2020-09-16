package com.ludovic.vimont.deezeropenapi.helper

import android.graphics.Bitmap

object BitmapHelper {
    private const val DEFAULT_WIDTH = 500
    private const val DEFAULT_HEIGHT = 500

    /**
     * Used to provide a default bitmap, if we can't succeed to retrieve the cover album.
     */
    fun emptyBitmap(): Bitmap {
        val conf = Bitmap.Config.ARGB_8888
        return Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, conf)
    }
}