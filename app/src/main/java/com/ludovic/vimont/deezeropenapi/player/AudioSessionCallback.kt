package com.ludovic.vimont.deezeropenapi.player

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
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
        const val KEY_ACTION_FROM = "media_session_key_action_from"
    }
    private var currentIndex = -1
    private val audioPlayer = AudioPlayer()
    private val mediaNotification = MediaNotificationBuilder()

    init {
        audioPlayer.onMusicEnd = {
            onSkipToNext()
        }
    }

    /**
     * We implement the onCustomAction to pass the queue by intent and thus set the playlist of all
     * the track preview of an album.
     */
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
                    setCurrentMetaData(currentTrackPosition, ActionFrom.UI)
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
    private fun setCurrentMetaData(currentTrackPosition: Int, actionFrom: ActionFrom = ActionFrom.NOTIFICATION) {
        if (mediaSession.controller.queue.size > currentTrackPosition) {
            val bundle = Bundle()
            bundle.putInt(KEY_CURRENT_TRACK, currentTrackPosition)
            bundle.putString(KEY_ACTION_FROM, actionFrom.toString())
            mediaSession.setExtras(bundle)

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
        mediaNotification.buildNotification(context, mediaSession, currentIndex, true)?.apply {
            service.startForeground(MediaNotificationBuilder.NOTIFICATION_ID, this)
        }
    }

    override fun onPause() {
        val context: Context = service.applicationContext
        val positionInCurrentTrack: Long = mediaSession.controller.playbackState.position
        val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_PAUSED, positionInCurrentTrack)
        mediaSession.setPlaybackState(playBackState)
        audioPlayer.stop()

        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification? = mediaNotification.buildNotification(context, mediaSession, currentIndex)
        notificationManager.notify(MediaNotificationBuilder.NOTIFICATION_ID, notification)
        service.stopForeground(false)
    }

    override fun onStop() {
        service.stopSelf()
        mediaSession.isActive = false
        mediaSession.release()
        audioPlayer.release()
        service.stopForeground(false)
    }

    override fun onSeekTo(desiredPosition: Long) {
        super.onSeekTo(desiredPosition)
        val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_PLAYING, desiredPosition)
        mediaSession.setPlaybackState(playBackState)
        audioPlayer.seekTo(desiredPosition.toInt())
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        currentIndex--
        if (currentIndex < 0) {
            currentIndex = 0
            return
        }
        setCurrentMetaData(currentIndex)

        val currentPlayBackState: Int = mediaSession.controller.playbackState.state
        val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, 0, currentIndex)
        mediaSession.setPlaybackState(playBackState)
        adaptPlayerState(currentPlayBackState)
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        currentIndex++
        if (currentIndex >= mediaSession.controller.queue.size) {
            currentIndex = mediaSession.controller.queue.size - 1
            onPause()
            return
        }
        setCurrentMetaData(currentIndex)

        val currentPlayBackState: Int = mediaSession.controller.playbackState.state
        val playBackState: PlaybackStateCompat? = AudioHelper.getPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, 0, currentIndex)
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