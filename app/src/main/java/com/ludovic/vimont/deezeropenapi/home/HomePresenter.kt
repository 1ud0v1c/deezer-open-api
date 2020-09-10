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
            403 -> {
                homeView.showErrorMessage("You weren't authorized to make your request; most likely this indicates an issue with your API Key.")
            }
            429 -> {
                homeView.showErrorMessage("Your API Key is making too many requests. Read about requesting a Production Key to upgrade your API Key rate limits.")
            }
            else -> {
                homeView.showErrorMessage(errorMessage)
            }
        }
    }
}