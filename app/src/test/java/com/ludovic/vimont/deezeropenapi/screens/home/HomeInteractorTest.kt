package com.ludovic.vimont.deezeropenapi.screens.home

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.ludovic.vimont.deezeropenapi.NetworkMock
import com.ludovic.vimont.deezeropenapi.api.DeezerAPI
import com.ludovic.vimont.deezeropenapi.api.DeezerService
import com.ludovic.vimont.deezeropenapi.model.Album
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
import java.util.concurrent.TimeUnit

@Config(sdk = [Build.VERSION_CODES.P], manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class HomeInteractorTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var homeInteractor: HomeInteractor
    private lateinit var homeInteractorResult: HomeInteractorResult
    private val mainThreadSurrogate: ExecutorCoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        homeInteractor = HomeInteractor()
        homeInteractorResult = HomeInteractorResult()
        homeInteractor.homeContractInteractor = homeInteractorResult
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
        // First, we try to launch request with no active network connection (default state for unit tests)
        homeInteractor.fetchAlbums(context)
        while (!homeInteractorResult.isRequestFinish) {
            Thread.sleep(10)
        }
        Assert.assertTrue(homeInteractorResult.albums.isEmpty())
        Assert.assertEquals(-1, homeInteractorResult.statusCode)
        Assert.assertFalse(homeInteractorResult.errorMessage.isEmpty())

        // Second try, but this time we mock a network connection
        homeInteractorResult.reset()
        NetworkMock.mockNetworkAccess(context)

        homeInteractor.fetchAlbums(context)
        while (!homeInteractorResult.isRequestFinish) {
            Thread.sleep(10)
        }
        Assert.assertNotNull(homeInteractorResult.albums)
        Assert.assertTrue(homeInteractorResult.errorMessage.isEmpty())
    }

    @Test
    fun testFetchAlbumsTimeout() {
        DeezerService.setClient(1, TimeUnit.MILLISECONDS)
        NetworkMock.mockNetworkAccess(context)
        homeInteractor.fetchAlbums(context)
        while (!homeInteractorResult.isRequestFinish) {
            Thread.sleep(10)
        }
        Assert.assertFalse(homeInteractorResult.errorMessage.isEmpty())
        Assert.assertTrue(homeInteractorResult.errorMessage.contains("timeout"))
    }

    @Test
    fun testSearchLimit() {
        // In total the list contains 184 albums
        var offset = 0
        var currentPage = 0
        NetworkMock.mockNetworkAccess(context)

        // Stop just before the last request
        while (offset < 175) {
            homeInteractor.fetchAlbums(context, currentPage)
            while (!homeInteractorResult.isRequestFinish) {
                Thread.sleep(10)
            }
            Assert.assertEquals(
                DeezerAPI.Constants.NUMBER_OF_ITEM_PER_REQUEST * (currentPage + 1),
                homeInteractorResult.albums.size
            )

            homeInteractorResult.isRequestFinish = false
            offset += DeezerAPI.Constants.NUMBER_OF_ITEM_PER_REQUEST
            currentPage += 1
        }

        // Here we already make enough request to touch the maximum number of result
        homeInteractor.fetchAlbums(context, currentPage)
        while (!homeInteractorResult.isRequestFinish) {
            Thread.sleep(10)
        }
        Assert.assertEquals(184, homeInteractorResult.albums.size)

        // Now we should not ask again the same request and thus don't load 8 more items
        homeInteractorResult.reset()
        homeInteractor.fetchAlbums(context, currentPage)
        while (!homeInteractorResult.isRequestFinish) {
            Thread.sleep(10)
        }
        Assert.assertEquals(0, homeInteractorResult.albums.size)
    }

    class HomeInteractorResult : HomeContract.Interactor {
        var isRequestFinish: Boolean = false
        var albums = ArrayList<Album>()
        var statusCode: Int = -1
        var errorMessage: String = ""

        override fun onSuccess(albums: List<Album>) {
            this.albums.addAll(albums)
            this.isRequestFinish = true
        }

        override fun onFail(statusCode: Int, errorMessage: String) {
            this.statusCode = statusCode
            this.errorMessage = errorMessage
            this.isRequestFinish = true
        }

        fun reset() {
            albums.clear()
            statusCode = -1
            errorMessage = ""
            isRequestFinish = false
        }
    }
}