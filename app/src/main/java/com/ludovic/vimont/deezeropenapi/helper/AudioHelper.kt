package com.ludovic.vimont.deezeropenapi.helper

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.ludovic.vimont.deezeropenapi.model.Album
import com.ludovic.vimont.deezeropenapi.model.Track

object AudioHelper {
    private const val PREVIEW_DURATION_IN_MS: Long = 30_000

    fun buildMediaDescriptionFromTrack(album: Album, track: Track, bitmap: Bitmap?): MediaDescriptionCompat {
        val songDuration = Bundle()
        songDuration.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, PREVIEW_DURATION_IN_MS)

        return MediaDescriptionCompat.Builder()
            .setTitle(track.title)
            .setSubtitle(album.artist?.name)
            .setDescription(track.link)
            .setMediaUri(Uri.parse(track.preview))
            .setIconBitmap(bitmap)
            .setExtras(songDuration)
            .build()

    }
}