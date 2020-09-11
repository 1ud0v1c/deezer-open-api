package com.ludovic.vimont.deezeropenapi.model

import android.os.Build
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
        album = buildAlbum(albumId)
    }

    private fun buildAlbum(albumId: Int, alternativeAlbum: Album? = null): Album {
        val artist = Artist(
            892, "Coldplay",
            "http://api.deezer.com/2.0/artist/892/image",
            "http://cdn-images.deezer.com/images/artist/04cf6c8a81a23e65663e7362d98d5ad9/56x56-000000-80-0-0.jpg",
            "http://cdn-images.deezer.com/images/artist/04cf6c8a81a23e65663e7362d98d5ad9/250x250-000000-80-0-0.jpg",
            "http://cdn-images.deezer.com/images/artist/04cf6c8a81a23e65663e7362d98d5ad9/500x500-000000-80-0-0.jpg",
            "http://cdn-images.deezer.com/images/artist/04cf6c8a81a23e65663e7362d98d5ad9/1000x1000-000000-80-0-0.jpg",
            "http://api.deezer.com/2.0/artist/892/top?limit=50"
        )
        return Album(
            albumId, "A Rush of Blood to the Head", "http://www.deezer.com/album/299821",
            "http://api.deezer.com/2.0/album/299821/image",
            "http://cdn-images.deezer.com/images/cover/5ba1787e1ec36dbbca38ff01fea8fb21/56x56-000000-80-0-0.jpg",
            "http://cdn-images.deezer.com/images/cover/5ba1787e1ec36dbbca38ff01fea8fb21/250x250-000000-80-0-0.jpg",
            "http://cdn-images.deezer.com/images/cover/5ba1787e1ec36dbbca38ff01fea8fb21/500x500-000000-80-0-0.jpg",
            "http://cdn-images.deezer.com/images/cover/5ba1787e1ec36dbbca38ff01fea8fb21/1000x1000-000000-80-0-0.jpg",
            11, releaseDate, "album", alternativeAlbum, "http://api.deezer.com/2.0/album/299821/tracks",
            artist, false, 1398530279
        )
    }

    @Test
    fun testGetId() {
        // We don't have any alternative for now, we should get the default id
        Assert.assertEquals(albumId, album.getId())

        // Now, we have an alternative album, we should have the alternative album id
        album = buildAlbum(albumId, buildAlbum(alternativeAlbumId))
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