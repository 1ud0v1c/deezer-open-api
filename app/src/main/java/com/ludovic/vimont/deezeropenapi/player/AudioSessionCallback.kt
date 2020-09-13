package com.ludovic.vimont.deezeropenapi.player

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat

class AudioSessionCallback(private val service: MediaPlaybackService,
                           private val mediaSession: MediaSessionCompat) : MediaSessionCompat.Callback() {
    private val audioPlayer = AudioPlayer()
    private val mediaNotification = MediaNotificationBuilder()

    override fun onAddQueueItem(description: MediaDescriptionCompat?) {
        super.onAddQueueItem(description)

        description?.let {
            val mediaMedata: MediaMetadataCompat = MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, description.iconBitmap)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, description.title.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, description.subtitle.toString())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, description.extras?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) ?: 0)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, description.mediaUri.toString())
                .build()
            mediaSession.setMetadata(mediaMedata)

            val playBackState = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f)
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackStateCompat.ACTION_SEEK_TO
                )
                .build()
            mediaSession.setPlaybackState(playBackState)
        }
    }

    override fun onPlay() {
        val context: Context = service.applicationContext
        ContextCompat.startForegroundService(context, Intent(context, MediaPlaybackService::class.java))
        mediaSession.isActive = true
        val playBackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PLAYING, mediaSession.controller.playbackState.position, 1.0f)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SEEK_TO
            )
            .build()
        mediaSession.setPlaybackState(playBackState)
        audioPlayer.play(mediaSession.controller.metadata.description.mediaUri.toString())
        service.startForeground(MediaNotificationBuilder.NOTIFICATION_ID, mediaNotification.buildNotification(context, mediaSession, true))
    }

    override fun onPause() {
        val context: Context = service.applicationContext
        val playBackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PAUSED, mediaSession.controller.playbackState.position, 1.0f)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SEEK_TO
            )
            .build()
        mediaSession.setPlaybackState(playBackState)
        audioPlayer.stop()
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(MediaNotificationBuilder.NOTIFICATION_ID, mediaNotification.buildNotification(context, mediaSession))
        service.stopForeground(false)
    }

    override fun onStop() {
        service.stopSelf()
        mediaSession.isActive = false
        val playBackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_STOPPED, 0, 1.0f)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SEEK_TO
            )
            .build()
        mediaSession.setPlaybackState(playBackState)
        audioPlayer.release()
        service.stopForeground(false)
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        val playBackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_PAUSED, pos, 1.0f)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SEEK_TO
            )
            .build()
        mediaSession.setPlaybackState(playBackState)
        audioPlayer.seekTo(pos.toInt())
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        println("onSkipToPrevious")
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        println("onSkipToNext")
    }
}