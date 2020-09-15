package com.ludovic.vimont.deezeropenapi.screens.detail

import android.content.ComponentName
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.ludovic.vimont.deezeropenapi.player.ActionFrom
import com.ludovic.vimont.deezeropenapi.player.AudioSessionCallback
import com.ludovic.vimont.deezeropenapi.player.MediaPlaybackService

/**
 * Handle the exchange between MediaSession & the DetailActivity
 */
class DetailMediaSessionHandler(private val detailActivity: DetailActivity): MediaBrowserCompat.ConnectionCallback(), LifecycleObserver {
    companion object {
        val TAG: String = DetailMediaSessionHandler::class.java.simpleName
    }
    private lateinit var mediaBrowser: MediaBrowserCompat

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        val componentName = ComponentName(detailActivity, MediaPlaybackService::class.java)
        mediaBrowser = MediaBrowserCompat(detailActivity, componentName, this, null)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        mediaBrowser.connect()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        detailActivity.volumeControlStream = AudioManager.STREAM_MUSIC
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        MediaControllerCompat.getMediaController(detailActivity)?.unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
    }

    override fun onConnected() {
        super.onConnected()
        mediaBrowser.sessionToken.also { token ->
            val mediaController = MediaControllerCompat(detailActivity, token)
            MediaControllerCompat.setMediaController(detailActivity, mediaController)
        }
        val mediaController: MediaControllerCompat = MediaControllerCompat.getMediaController(detailActivity)
        mediaController.registerCallback(controllerCallback)
        detailActivity.computeQueue(mediaController)
    }

    override fun onConnectionSuspended() {
        Log.e(TAG, "onConnectionSuspended, the Service has crashed.")
    }

    override fun onConnectionFailed() {
        Log.e(TAG, "onConnectionFailed, the Service has refused our connection.")
    }

    private var controllerCallback: MediaControllerCompat.Callback = object : MediaControllerCompat.Callback() {
        private var lastIndex: Int = -1
        private var from: ActionFrom? = null

        override fun onExtrasChanged(extras: Bundle?) {
            super.onExtrasChanged(extras)
            extras?.let { bundle ->
                if (bundle.containsKey(AudioSessionCallback.KEY_CURRENT_TRACK)) {
                    lastIndex = bundle.getInt(AudioSessionCallback.KEY_CURRENT_TRACK)
                }
                if (bundle.containsKey(AudioSessionCallback.KEY_ACTION_FROM)) {
                    bundle.getString(AudioSessionCallback.KEY_ACTION_FROM)?.let {
                        from = ActionFrom.valueOf(it)
                    }
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            if (canChangeUI()) {
                detailActivity.updateAdapterViewHolderState(lastIndex)
            }
        }

        private fun canChangeUI(): Boolean {
            return (ActionFrom.NOTIFICATION == from && lastIndex != -1)
        }
    }

    /**
     * After clicking on DetailTrackAdapter item, we try to change the current track, by sending a
     * custom action to the AudioSessionCallback.
     */
    fun askMediaSessionToSetCurrentTrack(clickedPosition: Int, playingNeeded: Boolean) {
        val mediaController: MediaControllerCompat = MediaControllerCompat.getMediaController(detailActivity)
        val bundle = Bundle()
        bundle.putInt(AudioSessionCallback.KEY_CURRENT_TRACK, clickedPosition)
        mediaController.transportControls.sendCustomAction(
            AudioSessionCallback.MEDIA_SESSION_ACTION_SET_CURRENT_TRACK, bundle
        )
        if (playingNeeded) {
            mediaController.transportControls.play()
        } else {
            mediaController.transportControls.pause()
        }
    }
}