package com.ludovic.vimont.deezeropenapi.screens.detail

import android.content.Context
import android.util.Log
import com.ludovic.vimont.deezeropenapi.api.DeezerService
import com.ludovic.vimont.deezeropenapi.helper.NetworkHelper
import com.ludovic.vimont.deezeropenapi.model.Album
import com.ludovic.vimont.deezeropenapi.model.Track
import com.ludovic.vimont.deezeropenapi.model.TrackResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

/**
 * Aimed to call Deezer's OpenAPI to fetch all album of an user.
 * We use a CoroutineExceptionHandler to catch error or timeout of the requests and thus dispatch
 * a result to the user.
 * @see: http://api.deezer.com/2.0/user/2529/albums
 */
class DetailInteractor {
    companion object {
        val TAG: String = DetailInteractor::class.java.simpleName
    }
    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, "An error occurred while fetching data.", exception)
        val timeoutErrorMessage = "The request failed because of a timeout. Please retry later or with a better connection."
        dispatchError(-1, timeoutErrorMessage)
    }
    var detailContractInteractor: DetailContract.Interactor? = null

    fun fetchTracks(context: Context, albumId: Int) {
        GlobalScope.launch(exceptionHandler) {
            if (!NetworkHelper.isOnline(context)) {
                dispatchError(-1, "You are not connected to internet. Try to refresh while being connected.")
                return@launch
            }
            val tracksRequest: Call<TrackResponse> = DeezerService.getAPI().getTracks(albumId)
            val result: Response<TrackResponse> = tracksRequest.execute()
            if (result.isSuccessful) {
                result.body()?.let { response ->
                    when {
                        response.total > 0 -> {
                            dispatchResult(response.data)
                        }
                        result.code() == 200 -> {
                            dispatchError(result.code(), "This album has no track bind to it.")
                        }
                        else -> {
                            dispatchError(result.code(), result.errorBody().toString())
                        }
                    }
                }
            }
        }
    }

    private fun dispatchResult(tracks: List<Track>) {
        GlobalScope.launch(Dispatchers.Main) {
            detailContractInteractor?.onSuccess(tracks)
        }
    }

    private fun dispatchError(statusCode: Int, errorMessage: String) {
        GlobalScope.launch(Dispatchers.Main) {
            detailContractInteractor?.onFail(statusCode, errorMessage)
        }
    }
}