package com.ludovic.vimont.deezeropenapi.player

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver

private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"

/**
 * Create and handle exchange between the UI and the service, thanks and trough a MediaSession object
 */
class MediaPlaybackService : MediaBrowserServiceCompat() {
    companion object {
        val TAG: String = MediaPlaybackService::class.java.simpleName
    }
    private var mediaSession: MediaSessionCompat? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    override fun onCreate() {
        super.onCreate()

        mediaSession = MediaSessionCompat(baseContext, TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
            val playStateActions = PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE
            stateBuilder = PlaybackStateCompat.Builder().setActions(playStateActions)
            setPlaybackState(stateBuilder.build())
            setCallback(AudioSessionCallback( this@MediaPlaybackService, this))
            setSessionToken(sessionToken)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        if (MY_EMPTY_MEDIA_ROOT_ID == parentId) {
            result.sendResult(null)
            return
        }
    }
}