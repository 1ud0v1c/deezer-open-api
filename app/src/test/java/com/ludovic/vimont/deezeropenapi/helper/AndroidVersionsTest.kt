package com.ludovic.vimont.deezeropenapi.helper

import android.os.Build
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class AndroidVersionsTest {
    @Config(sdk = [Build.VERSION_CODES.KITKAT])
    @Test
    fun testInferiorOrEqualAtKitKat() {
        Assert.assertTrue(AndroidVersions.inferiorOrEqualAtKitKat)
    }

    @Config(sdk = [Build.VERSION_CODES.P])
    @Test
    fun testInferiorOrEqualAtKitKatForAndroidP() {
        Assert.assertFalse(AndroidVersions.inferiorOrEqualAtKitKat)
    }

    @Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
    @Test
    fun testAtLeastLollipop() {
        Assert.assertTrue(AndroidVersions.atLeastLollipop)
    }

    @Config(sdk = [Build.VERSION_CODES.KITKAT])
    @Test
    fun testAtLeastLollipopForKitKat() {
        Assert.assertFalse(AndroidVersions.atLeastLollipop)
    }

    @Config(sdk = [Build.VERSION_CODES.O])
    @Test
    fun testAtLeastOreo() {
        Assert.assertTrue(AndroidVersions.atLeastOreo)
    }

    @Config(sdk = [Build.VERSION_CODES.KITKAT])
    @Test
    fun testAtLeastOreoForKiKat() {
        Assert.assertFalse(AndroidVersions.atLeastOreo)
    }
}