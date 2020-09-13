package com.ludovic.vimont.deezeropenapi

import com.ludovic.vimont.deezeropenapi.model.Album
import com.ludovic.vimont.deezeropenapi.model.Artist
import com.ludovic.vimont.deezeropenapi.model.Track
import java.util.*
import kotlin.random.Random.Default.nextInt

object ModelMock {
    fun buildAlbum(
        albumId: Int = nextInt(1, 1000),
        alternativeAlbum: Album? = null,
        releaseDate: String = "2002-08-08"
    ): Album {
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
            albumId,
            "A Rush of Blood to the Head",
            "http://www.deezer.com/album/299821",
            "http://api.deezer.com/2.0/album/299821/image",
            "http://cdn-images.deezer.com/images/cover/5ba1787e1ec36dbbca38ff01fea8fb21/56x56-000000-80-0-0.jpg",
            "http://cdn-images.deezer.com/images/cover/5ba1787e1ec36dbbca38ff01fea8fb21/250x250-000000-80-0-0.jpg",
            "http://cdn-images.deezer.com/images/cover/5ba1787e1ec36dbbca38ff01fea8fb21/500x500-000000-80-0-0.jpg",
            "http://cdn-images.deezer.com/images/cover/5ba1787e1ec36dbbca38ff01fea8fb21/1000x1000-000000-80-0-0.jpg",
            11,
            releaseDate,
            "album",
            alternativeAlbum,
            "http://api.deezer.com/2.0/album/299821/tracks",
            artist,
            false,
            1398530279
        )
    }

    fun buildTrack(trackId: Int = nextInt(1, 1000)): Track {
        return Track(
            trackId,
            true,
            UUID.randomUUID().toString(),
            "System of a Down",
            "System of a Down",
            "https://www.deezer.com/en/track/61064534",
            "https://www.deezer.com/en/track/61064534",
            198,
            1,
            15_616,
            false,
            0,
            0,
            11
        )
    }
}