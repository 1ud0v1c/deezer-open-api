package com.ludovic.vimont.deezeropenapi.home

import android.content.Context
import com.ludovic.vimont.deezeropenapi.model.Album

class HomePresenter(
    private val homeView: HomeContract.View,
    private val homeInteractor: HomeInteractor) : HomeContract.Interactor {

    init {
        homeInteractor.homeContractInteractor = this
    }

    fun start(context: Context, currentPage: Int = 0) {
        homeInteractor.fetchAlbums(context, currentPage)
    }

    override fun onSuccess(albums: List<Album>) {
        homeView.setAlbums(albums)
    }

    override fun onFail(statusCode: Int, errorMessage: String) {
        when (statusCode) {
            400 -> {
                homeView.showErrorMessage("Your request was formatted incorrectly or missing a required parameter(s)..")
            }
            401 -> {
                homeView.showErrorMessage("You weren't authorized to make your request.")
            }
            404 -> {
                homeView.showErrorMessage("API not found, this url has not been recognized by the server.")
            }
            429 -> {
                homeView.showErrorMessage("You made too many requests in a short period of time, please retry later.")
            }
            else -> {
                homeView.showErrorMessage(errorMessage)
            }
        }
    }
}