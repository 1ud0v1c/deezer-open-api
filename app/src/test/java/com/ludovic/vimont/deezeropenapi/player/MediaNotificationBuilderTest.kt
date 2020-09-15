package com.ludovic.vimont.deezeropenapi.player

import android.app.Notification
import android.content.Context
import android.os.Build
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.test.core.app.ApplicationProvider
import com.ludovic.vimont.deezeropenapi.ModelMock
import com.ludovic.vimont.deezeropenapi.R
import com.ludovic.vimont.deezeropenapi.helper.AudioHelper
import com.ludovic.vimont.deezeropenapi.model.Album
import com.ludovic.vimont.deezeropenapi.model.Track
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O], manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class MediaNotificationBuilderTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var mediaNotificationBuilder: MediaNotificationBuilder
    private lateinit var mediaSessionCompat: MediaSessionCompat

    @Before
    fun setUp() {
        mediaNotificationBuilder = MediaNotificationBuilder()
        mediaSessionCompat = mock(MediaSessionCompat::class.java)
    }

    @Test
    fun testBuildNotification() {
        val album: Album = ModelMock.buildAlbum()
        val track: Track = ModelMock.buildTrack()

        val metaDescriptionCompat: MediaDescriptionCompat = AudioHelper.buildMediaDescriptionFromTrack(album, track)
        val metadataCompat: MediaMetadataCompat = AudioHelper.mediaDescriptionToMediaMetadata(metaDescriptionCompat)
        val mediaControllerCompat: MediaControllerCompat = mock(MediaControllerCompat::class.java)

        Mockito.`when`(mediaSessionCompat.controller).thenReturn(mediaControllerCompat)
        Mockito.`when`(mediaSessionCompat.controller.metadata).thenReturn(metadataCompat)

        // Did we succeed to create a notification
        val notification: Notification? = mediaNotificationBuilder.buildNotification(context, mediaSessionCompat, 0)
        Assert.assertNotNull(notification)

        // Is it, displaying play possibility
        notification?.let {
            var hasBeenFound = false
            for (action: Notification.Action in it.actions) {
                if (context.getString(R.string.player_play) == action.title) {
                    hasBeenFound = true
                }
            }
            Assert.assertTrue(hasBeenFound)
        }

        val newNotification: Notification? = mediaNotificationBuilder.buildNotification(context, mediaSessionCompat, 0, true)
        Assert.assertNotNull(newNotification)

        // Is it, displaying pause possibility
        newNotification?.let {
            var hasBeenFound = false
            for (action: Notification.Action in it.actions) {
                if (context.getString(R.string.player_pause) == action.title) {
                    hasBeenFound = true
                }
            }
            Assert.assertTrue(hasBeenFound)
        }
    }
}