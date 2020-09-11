package com.ludovic.vimont.deezeropenapi.helper

import android.os.Build

/**
 * Simplifying code readability
 */
object AndroidVersions {
    val inferiorOrEqualAtKitKat: Boolean = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT

    val atLeastLollipop: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    val atLeastOreo: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}