package com.ludovic.vimont.deezeropenapi.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.ludovic.vimont.deezeropenapi.R

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

            val playStateActions: Long = PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE
            stateBuilder = PlaybackStateCompat.Builder().setActions(playStateActions)
            setPlaybackState(stateBuilder.build())

            val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
            mediaButtonIntent.setClass(applicationContext, MediaButtonReceiver::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, 0)
            setMediaButtonReceiver(pendingIntent)

            setCallback(AudioSessionCallback(this@MediaPlaybackService, this))
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
        if (TextUtils.equals(clientPackageName, packageName)) {
            return BrowserRoot(getString(R.string.app_name), null)
        }
        return null
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }
}