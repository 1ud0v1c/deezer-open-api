package com.ludovic.vimont.deezeropenapi.screens.detail

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.ludovic.vimont.deezeropenapi.NetworkMock
import com.ludovic.vimont.deezeropenapi.api.DeezerService
import com.ludovic.vimont.deezeropenapi.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.Executors

@Config(sdk = [Build.VERSION_CODES.P], manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class DetailInteractorTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var detailInteractor: DetailInteractor
    private lateinit var detailInteractorResult: DetailInteractorResult
    private val mainThreadSurrogate: ExecutorCoroutineDispatcher =
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        detailInteractor = DetailInteractor()
        detailInteractorResult = DetailInteractorResult()
        detailInteractor.detailContractInteractor = detailInteractorResult
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
        DeezerService.setClient(OkHttpClient.Builder().build())
    }

    @Test
    fun testFetchAlbums() {
        // Coldplay Album - A Rush of Blood to the Head, we should find the song "The Scientist"
        val albumId = 299_821
        val songName = "The Scientist"

        // First, we try to launch request with no active network connection (default state for unit tests)
        detailInteractor.fetchTracks(context, albumId)
        while (!detailInteractorResult.isRequestFinish) {
            Thread.sleep(10)
        }
        Assert.assertTrue(detailInteractorResult.tracks.isEmpty())
        Assert.assertEquals(-1, detailInteractorResult.statusCode)
        Assert.assertFalse(detailInteractorResult.errorMessage.isEmpty())

        // Second try, but this time we mock a network connection
        detailInteractorResult.reset()
        NetworkMock.mockNetworkAccess(context)

        detailInteractor.fetchTracks(context, albumId)
        while (!detailInteractorResult.isRequestFinish) {
            Thread.sleep(10)
        }
        Assert.assertTrue(detailInteractorResult.tracks.isNotEmpty())
        Assert.assertTrue(detailInteractorResult.errorMessage.isEmpty())

        var hasTheScientistSongBeenFind = false
        for (track: Track in detailInteractorResult.tracks) {
            if (track.title_short.toLowerCase() == songName.toLowerCase()) {
                hasTheScientistSongBeenFind = true
            }
        }
        Assert.assertTrue(hasTheScientistSongBeenFind)
    }

    class DetailInteractorResult : DetailContract.Interactor {
        var isRequestFinish: Boolean = false
        var tracks = ArrayList<Track>()
        var statusCode: Int = -1
        var errorMessage: String = ""

        override fun onSuccess(tracks: List<Track>) {
            this.tracks.addAll(tracks)
            this.isRequestFinish = true
        }

        override fun onFail(statusCode: Int, errorMessage: String) {
            this.statusCode = statusCode
            this.errorMessage = errorMessage
            this.isRequestFinish = true
        }

        fun reset() {
            tracks.clear()
            statusCode = -1
            errorMessage = ""
            isRequestFinish = false
        }
    }
}