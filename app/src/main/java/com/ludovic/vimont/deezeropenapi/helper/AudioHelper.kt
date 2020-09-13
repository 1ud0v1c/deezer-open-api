package com.ludovic.vimont.deezeropenapi.helper

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.ludovic.vimont.deezeropenapi.model.Album
import com.ludovic.vimont.deezeropenapi.model.Track

object AudioHelper {
    private const val PREVIEW_DURATION_IN_MS: Long = 30_000
    private const val PLAYBACK_SPEED: Float = 1.0f

    private val mediaMetadataBuilder = MediaMetadataCompat.Builder()
    private val mediaDescriptionBuilder = MediaDescriptionCompat.Builder()
    private val playbackStateCompatBuilder = PlaybackStateCompat.Builder()

    fun buildMediaDescriptionFromTrack(album: Album, track: Track, bitmap: Bitmap? = null): MediaDescriptionCompat {
        val songDuration = Bundle()
        songDuration.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, PREVIEW_DURATION_IN_MS)

        return mediaDescriptionBuilder.setTitle(track.title)
            .setSubtitle(album.artist?.name)
            .setDescription(track.link)
            .setMediaUri(Uri.parse(track.preview))
            .setIconBitmap(bitmap)
            .setExtras(songDuration)
            .build()
    }

    fun mediaDescriptionToMediaMetadata(mediaDescription: MediaDescriptionCompat): MediaMetadataCompat {
        return mediaMetadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, mediaDescription.iconBitmap)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaDescription.title.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, mediaDescription.subtitle.toString())
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaDescription.extras?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) ?: 0)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaDescription.mediaUri.toString())
            .build()
    }

    fun getPlaybackState(playbackState: Int, positionInCurrentTrack: Long): PlaybackStateCompat? {
        return playbackStateCompatBuilder.setState(playbackState, positionInCurrentTrack, PLAYBACK_SPEED)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SEEK_TO
            )
            .build()
    }
}