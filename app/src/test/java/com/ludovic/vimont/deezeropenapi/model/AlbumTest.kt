package com.ludovic.vimont.deezeropenapi.model

import android.os.Build
import com.ludovic.vimont.deezeropenapi.ModelMock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P], manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class AlbumTest {
    private val albumId: Int = 299_821
    private val alternativeAlbumId: Int = 5_299_821
    private val releaseDate = "2002-08-08"
    private lateinit var album: Album

    @Before
    fun setUp() {
        album = ModelMock.buildAlbum(albumId)
    }

    @Test
    fun testGetId() {
        // We don't have any alternative for now, we should get the default id
        Assert.assertEquals(albumId, album.getId())

        // Now, we have an alternative album, we should have the alternative album id
        album = ModelMock.buildAlbum(albumId, ModelMock.buildAlbum(alternativeAlbumId))
        Assert.assertEquals(alternativeAlbumId, album.getId())
    }

    @Test
    fun testGetReleaseDate() {
        val desireReleaseDate: String = releaseDate.split("-").reversed().joinToString(separator = "/") { it }
        Assert.assertEquals(desireReleaseDate, album.getReleaseDate())

        val newDesiredReleaseDate = "Thu, 8 Aug 2002"
        val desiredFormat = "EEE, d MMM yyyy"
        Assert.assertEquals(newDesiredReleaseDate, album.getReleaseDate(desiredFormat))
    }
}