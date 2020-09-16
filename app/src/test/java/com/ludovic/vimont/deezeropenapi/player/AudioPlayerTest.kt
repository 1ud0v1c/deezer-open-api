package com.ludovic.vimont.deezeropenapi.player

import android.os.Build
import android.os.Looper
import com.ludovic.vimont.deezeropenapi.ModelMock
import com.ludovic.vimont.deezeropenapi.model.Track
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.shadows.util.DataSource

@Config(sdk = [Build.VERSION_CODES.P], manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class AudioPlayerTest {
    private var isReadyTopPlay= false
    private lateinit var track: Track
    private lateinit var audioPlayer: AudioPlayer

    @Before
    fun setUp() {
        track = ModelMock.buildTrack()
        audioPlayer = AudioPlayer()
        audioPlayer.onMusicReady = {
            isReadyTopPlay = true
        }

        ShadowMediaPlayer.addMediaInfo(DataSource.toDataSource(track.preview, HashMap<String, String>()),
            ShadowMediaPlayer.MediaInfo(150, 0)
        )
    }

    @After
    fun tearDown() {
        audioPlayer.release()
        isReadyTopPlay = false
    }

    @Test
    fun testIsPlaying() {
        Assert.assertFalse(audioPlayer.isPlaying())
        audioPlayer.play(track.preview)
        while (!isReadyTopPlay) {
            Thread.sleep(25)
            shadowOf(Looper.getMainLooper()).idle()
        }
        Assert.assertTrue(audioPlayer.isPlaying())
    }

    @Test
    fun testSeekTo() {
        Assert.assertEquals(0, audioPlayer.getCurrentPosition())
        Assert.assertFalse(audioPlayer.isPlaying())
        audioPlayer.play(track.preview)
        while (!isReadyTopPlay) {
            Thread.sleep(25)
            shadowOf(Looper.getMainLooper()).idle()
        }
        audioPlayer.seekTo(50)
        Assert.assertEquals(50, audioPlayer.getCurrentPosition())
    }
}