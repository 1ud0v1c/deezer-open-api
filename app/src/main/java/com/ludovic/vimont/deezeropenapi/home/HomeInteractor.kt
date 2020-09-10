package com.ludovic.vimont.deezeropenapi.home

import com.ludovic.vimont.deezeropenapi.api.DeezerAPI
import com.ludovic.vimont.deezeropenapi.api.DeezerService
import com.ludovic.vimont.deezeropenapi.model.Album
import com.ludovic.vimont.deezeropenapi.model.AlbumResponse
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
    var homeContractInteractor: HomeContract.Interactor? = null

    fun fetchAlbums(currentPage: Int = 0) {
        val currentIndex: Int = currentPage * DeezerAPI.Constants.NUMBER_OF_ITEM_PER_REQUEST
        GlobalScope.launch {
            val albumsRequest: Call<AlbumResponse> = DeezerService.getAPI().getAlbums(currentIndex)
            val result: Response<AlbumResponse> = albumsRequest.execute()
            if (result.isSuccessful) {
                result.body()?.let { response ->
                    if (response.data.isNotEmpty()) {
                        dispatchResult(response.data)
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