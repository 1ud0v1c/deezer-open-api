package com.ludovic.vimont.deezeropenapi.player

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import com.ludovic.vimont.deezeropenapi.helper.AudioHelper

/**
 * Implements MediaSession Callback to ensure the communication between the UI & the background service
 */
class AudioSessionCallback(private val service: MediaPlaybackService,
                           private val mediaSession: MediaSessionCompat) : MediaSessionCompat.Callback() {
    private val audioPlayer = AudioPlayer()
    private val mediaNotification = MediaNotificationBuilder()

    override fun onAddQueueItem(description: MediaDescriptionCompat?) {
        super.onAddQueueItem(description)
        description?.let { mediaDescription ->
            val mediaMetaData: MediaMetadataCompat = AudioHelper.mediaDescriptionToMediaMetadata(mediaDescription)
            mediaSession.setMetadata(mediaMetaData)
            val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_PAUSED, 0)
            mediaSession.setPlaybackState(playBackState)
        }
    }

    override fun onPlay() {
        val context: Context = service.applicationContext
        ContextCompat.startForegroundService(context, Intent(context, MediaPlaybackService::class.java))
        mediaSession.isActive = true
        val positionInCurrentTrack: Long = mediaSession.controller.playbackState.position
        val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_PLAYING, positionInCurrentTrack)
        mediaSession.setPlaybackState(playBackState)
        audioPlayer.play(mediaSession.controller.metadata.description.mediaUri.toString())
        service.startForeground(MediaNotificationBuilder.NOTIFICATION_ID, mediaNotification.buildNotification(context, mediaSession, true))
    }

    override fun onPause() {
        val context: Context = service.applicationContext
        val positionInCurrentTrack: Long = mediaSession.controller.playbackState.position
        val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_PAUSED, positionInCurrentTrack)
        mediaSession.setPlaybackState(playBackState)
        audioPlayer.stop()
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(MediaNotificationBuilder.NOTIFICATION_ID, mediaNotification.buildNotification(context, mediaSession))
        service.stopForeground(false)
    }

    override fun onStop() {
        service.stopSelf()
        mediaSession.isActive = false
        val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_STOPPED, 0)
        mediaSession.setPlaybackState(playBackState)
        audioPlayer.release()
        service.stopForeground(false)
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_PLAYING, pos)
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