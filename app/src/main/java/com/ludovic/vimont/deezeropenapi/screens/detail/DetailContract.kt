package com.ludovic.vimont.deezeropenapi.screens.detail

import com.ludovic.vimont.deezeropenapi.model.Track

interface DetailContract {
    interface View {
        fun setTracks(tracks: List<Track>)

        fun showErrorMessage(errorMessage: String)
    }

    interface Interactor {
        fun onSuccess(tracks: List<Track>)

        fun onFail(statusCode: Int, errorMessage: String)
    }
}