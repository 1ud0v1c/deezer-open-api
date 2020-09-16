package com.ludovic.vimont.deezeropenapi.screens.detail

import android.content.Context
import com.ludovic.vimont.deezeropenapi.model.Track

class DetailPresenter(
    private val detailView: DetailContract.View,
    private val detailInteractor: DetailInteractor
) : DetailContract.Interactor {
    init {
        detailInteractor.detailContractInteractor = this
    }

    fun start(context: Context, albumId: Int) {
        detailInteractor.fetchTracks(context, albumId)
    }

    override fun onSuccess(tracks: List<Track>) {
        detailView.setTracks(tracks)
    }

    override fun onFail(statusCode: Int, errorMessage: String) {
        when (statusCode) {
            -1 -> {
                detailView.showErrorMessage(errorMessage, true)
            }
            400 -> {
                detailView.showErrorMessage("Your request was formatted incorrectly or missing a required parameter(s)..")
            }
            401 -> {
                detailView.showErrorMessage("You weren't authorized to make your request.")
            }
            429 -> {
                detailView.showErrorMessage("You made too many requests in a short period of time, please retry later.")
            }
            else -> {
                detailView.showErrorMessage(errorMessage)
            }
        }
    }
}