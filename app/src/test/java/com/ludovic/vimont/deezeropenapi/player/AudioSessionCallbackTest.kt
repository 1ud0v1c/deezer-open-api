package com.ludovic.vimont.deezeropenapi.player

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper.getMainLooper
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.test.core.app.ApplicationProvider
import com.ludovic.vimont.deezeropenapi.ModelMock
import com.ludovic.vimont.deezeropenapi.helper.AudioHelper
import com.ludovic.vimont.deezeropenapi.helper.BitmapHelper
import com.ludovic.vimont.deezeropenapi.model.Album
import com.ludovic.vimont.deezeropenapi.model.Track
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.shadows.util.DataSource
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Config(sdk = [Build.VERSION_CODES.P], manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class AudioSessionCallbackTest {
    private var hasBeenCalled = false
    private val album: Album = ModelMock.buildAlbum()
    private val tracks = ArrayList<Track>()
    private lateinit var service: MediaPlaybackService
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var audioSessionCallback: AudioSessionCallback

    @Before
    fun setUp() {
        service = Robolectric.buildService(MediaPlaybackService::class.java).get()
        mediaSessionCompat = mock(MediaSessionCompat::class.java)
        audioSessionCallback = AudioSessionCallback(service, mediaSessionCompat)

        for (i: Int in 0 until 5) {
            tracks.add(ModelMock.buildTrack())
        }
        ShadowMediaPlayer.addMediaInfo(
            DataSource.toDataSource(tracks[0].preview, HashMap<String, String>()),
            ShadowMediaPlayer.MediaInfo(150, 0)
        )

        val mediaControllerCompat: MediaControllerCompat = mock(MediaControllerCompat::class.java)
        Mockito.`when`(mediaSessionCompat.controller).thenReturn(mediaControllerCompat)
        val playbackState: PlaybackStateCompat = mock(PlaybackStateCompat::class.java)
        Mockito.`when`(mediaSessionCompat.controller.playbackState).thenReturn(playbackState)
        val metadata: MediaMetadataCompat = mock(MediaMetadataCompat::class.java)
        Mockito.`when`(mediaSessionCompat.controller.metadata).thenReturn(metadata)
        val descriptionCompat: MediaDescriptionCompat = mock(MediaDescriptionCompat::class.java)
        Mockito.`when`(mediaSessionCompat.controller.metadata.description).thenReturn(descriptionCompat)
        Mockito.`when`(mediaSessionCompat.controller.metadata.description.mediaUri).thenReturn(Uri.parse(tracks[0].preview))
    }

    @Test
    fun testOnCustomAction() {
        Assert.assertEquals(0, mediaSessionCompat.controller.queue.size)

        val bundle = Bundle()
        val queue = ArrayList<MediaSessionCompat.QueueItem>()
        tracks.forEachIndexed { index, track ->
            AudioHelper.buildMediaDescriptionFromTrack(album, track, BitmapHelper.emptyBitmap()).let { mediaDescriptionCompat ->
                queue.add(MediaSessionCompat.QueueItem(mediaDescriptionCompat, index.toLong()))
            }
        }
        bundle.putParcelableArrayList(AudioSessionCallback.KEY_PLAYLIST, queue)
        doAnswer { invocation ->
            val queueToSet: ArrayList<Track> = invocation.arguments[0] as ArrayList<Track>
            Assert.assertEquals(5, queueToSet.size)
            hasBeenCalled = true
            queueToSet
        }.`when`(mediaSessionCompat).setQueue(any())

        audioSessionCallback.onCustomAction(AudioSessionCallback.MEDIA_SESSION_ACTION_SET_PLAYLIST, bundle)
        Assert.assertTrue(hasBeenCalled)
    }
}