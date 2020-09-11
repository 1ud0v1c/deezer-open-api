package com.ludovic.vimont.deezeropenapi.screens.home

import com.ludovic.vimont.deezeropenapi.model.Album

interface HomeContract {
    interface View {
        fun setAlbums(albums: List<Album>)

        fun showErrorMessage(errorMessage: String)
    }

    interface Interactor {
        fun onSuccess(albums: List<Album>)

        fun onFail(statusCode: Int, errorMessage: String)
    }
}