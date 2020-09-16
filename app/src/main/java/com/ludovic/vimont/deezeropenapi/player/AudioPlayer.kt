package com.ludovic.vimont.deezeropenapi.player

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.ludovic.vimont.deezeropenapi.helper.AndroidVersions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Little wrapper for MediaPlayer, to easily interact with it from the MediaSession callback.
 */
class AudioPlayer: MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private var mediaPlayer: MediaPlayer? = null
    private var previousURL: String? = null
    var onMusicReady: (() -> Unit)? = null
    var onMusicEnd: (() -> Unit)? = null

    init {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.isLooping = false
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnCompletionListener(this)
    }

    @SuppressLint("NewApi")
    fun play(url: String) {
        GlobalScope.launch {
            mediaPlayer?.let { mediaPlayer ->
                if (previousURL != null && previousURL.equals(url)) {
                    mediaPlayer.setOnCompletionListener(this@AudioPlayer)
                    mediaPlayer.start()
                } else {
                    stop()
                    mediaPlayer.reset()
                    if (AndroidVersions.atLeastLollipop) {
                        mediaPlayer.setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                    }
                    if (AndroidVersions.inferiorOrEqualAtKitKat) {
                        mediaPlayer.setDataSource(url.replace("https", "http"))
                    } else {
                        mediaPlayer.setDataSource(url)
                    }
                    mediaPlayer.prepareAsync()
                    previousURL = url
                }
            }
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    fun seekTo(position: Int) {
        if (isPlaying()) {
            mediaPlayer?.seekTo(position)
        }
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun stop() {
        if (isPlaying()) {
            mediaPlayer?.pause()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        previousURL = null
        onMusicEnd = null
        onMusicReady = null
    }

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.start()
        onMusicReady?.invoke()
    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        stop()
        mediaPlayer?.reset()
        onMusicEnd?.invoke()
    }
}