package com.ludovic.vimont.deezeropenapi.player

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.os.Bundle
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
    companion object {
        const val MEDIA_SESSION_ACTION_SET_PLAYLIST = "media_session_set_playlist"
        const val MEDIA_SESSION_ACTION_SET_CURRENT_TRACK = "media_session_set_current_track"
        const val KEY_PLAYLIST = "media_session_key_playlist"
        const val KEY_CURRENT_TRACK = "media_session_key_current_track"
    }
    private var currentIndex = -1
    private val audioPlayer = AudioPlayer()
    private val mediaNotification = MediaNotificationBuilder()

    override fun onCustomAction(action: String?, extras: Bundle?) {
        super.onCustomAction(action, extras)
        when (action) {
            MEDIA_SESSION_ACTION_SET_PLAYLIST -> {
                extras?.getParcelableArrayList<MediaSessionCompat.QueueItem>(KEY_PLAYLIST)?.let { newQueueToSet ->
                    mediaSession.setQueue(newQueueToSet)
                }
            }
            MEDIA_SESSION_ACTION_SET_CURRENT_TRACK -> {
                extras?.getInt(KEY_CURRENT_TRACK)?.let { currentTrackPosition ->
                    setCurrentMetaData(currentTrackPosition)
                    val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_PAUSED, 0)
                    mediaSession.setPlaybackState(playBackState)
                    currentIndex = currentTrackPosition
                }
            }
        }
    }

    /**
     * Set current MetaData based on items present in the current queue
     */
    private fun setCurrentMetaData(currentTrackPosition: Int) {
        if (mediaSession.controller.queue.size > currentTrackPosition) {
            val mediaDescription: MediaDescriptionCompat = mediaSession.controller.queue[currentTrackPosition].description
            val mediaMetaData: MediaMetadataCompat = AudioHelper.mediaDescriptionToMediaMetadata(mediaDescription)
            mediaSession.setMetadata(mediaMetaData)
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
        val notification = mediaNotification.buildNotification(context, mediaSession, currentIndex, true)
        service.startForeground(MediaNotificationBuilder.NOTIFICATION_ID, notification)
    }

    override fun onPause() {
        val context: Context = service.applicationContext
        val positionInCurrentTrack: Long = mediaSession.controller.playbackState.position
        val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_PAUSED, positionInCurrentTrack)
        mediaSession.setPlaybackState(playBackState)
        audioPlayer.stop()

        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = mediaNotification.buildNotification(context, mediaSession, currentIndex)
        notificationManager.notify(MediaNotificationBuilder.NOTIFICATION_ID, notification)
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
        currentIndex--
        if (currentIndex <= 0) {
            currentIndex = 0
        }
        setCurrentMetaData(currentIndex)

        val currentPlayBackState: Int = mediaSession.controller.playbackState.state
        val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, 0)
        mediaSession.setPlaybackState(playBackState)
        adaptPlayerState(currentPlayBackState)
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        currentIndex++
        if (currentIndex > mediaSession.controller.queue.size - 1) {
            currentIndex = mediaSession.controller.queue.size
            return
        }
        setCurrentMetaData(currentIndex)

        val currentPlayBackState: Int = mediaSession.controller.playbackState.state
        val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, 0)
        mediaSession.setPlaybackState(playBackState)
        adaptPlayerState(currentPlayBackState)
    }

    /**
     * Used to adapt the player state, based on the previous one, after changing the current song
     * using previous or next button
     */
    private fun adaptPlayerState(currentPlayBackState: Int) {
        when (currentPlayBackState) {
            PlaybackStateCompat.STATE_PLAYING -> {
                onPlay()
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                onPause()
            }
        }
    }
}