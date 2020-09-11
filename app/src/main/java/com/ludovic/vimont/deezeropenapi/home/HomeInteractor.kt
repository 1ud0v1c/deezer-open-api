package com.ludovic.vimont.deezeropenapi.home

import android.content.Context
import android.util.Log
import com.ludovic.vimont.deezeropenapi.api.DeezerAPI
import com.ludovic.vimont.deezeropenapi.api.DeezerService
import com.ludovic.vimont.deezeropenapi.helper.NetworkHelper
import com.ludovic.vimont.deezeropenapi.model.Album
import com.ludovic.vimont.deezeropenapi.model.AlbumResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

/**
 * Aimed to request the RandomEndpoint (@see: https://developers.giphy.com/docs/api/endpoint#random)
 * of the GIPHY API.
 */
class HomeInteractor {
    companion object {
        val TAG: String = HomeInteractor::class.java.simpleName
    }
    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, "An error occurred while fetching data.", exception)
        val timeoutErrorMessage = "The request failed because of a timeout. Please retry later or with a better connection."
        dispatchError(-1, timeoutErrorMessage)
    }
    var homeContractInteractor: HomeContract.Interactor? = null
    private var lastAlbumResponse: AlbumResponse? = null

    fun fetchAlbums(context: Context, currentPage: Int = 0) {
        val currentIndex: Int = currentPage * DeezerAPI.Constants.NUMBER_OF_ITEM_PER_REQUEST
        GlobalScope.launch(exceptionHandler) {
            if (!NetworkHelper.isOnline(context)) {
                dispatchError(-1, "You are not connected to internet. Try to refresh while being connected.")
                return@launch
            }
            val albumsRequest: Call<AlbumResponse> = DeezerService.getAPI().getAlbums(currentIndex)
            lastAlbumResponse?.let { albumResponse ->
                // If we arrive here, we touched the end of the list, there is nothing more to fetch.
                if (albumResponse.next == null) {
                    dispatchResult(ArrayList())
                    return@launch
                }
            }
            val result: Response<AlbumResponse> = albumsRequest.execute()
            if (result.isSuccessful) {
                result.body()?.let { response ->
                    if (response.data.isNotEmpty()) {
                        dispatchResult(response.data)
                        lastAlbumResponse = response
                    } else {
                        dispatchError(result.code(), result.errorBody().toString())
                    }
                }
            }
        }
    }

    private fun dispatchResult(albums: List<Album>) {
        GlobalScope.launch(Dispatchers.Main) {
            homeContractInteractor?.onSuccess(albums)
        }
    }

    private fun dispatchError(statusCode: Int, errorMessage: String) {
        GlobalScope.launch(Dispatchers.Main) {
            homeContractInteractor?.onFail(statusCode, errorMessage)
        }
    }
}