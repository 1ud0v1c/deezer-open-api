package com.ludovic.vimont.deezeropenapi.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ludovic.vimont.deezeropenapi.databinding.ActivityMainBinding
import com.ludovic.vimont.deezeropenapi.model.Album

/**
 * Entry point of the Application which will list of available albums of an user.
 */
class HomeActivity : AppCompatActivity(), HomeContract.View {
    private val homePresenter = HomePresenter(this, HomeInteractor())
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        homePresenter.start()
    }

    override fun setAlbums(albums: List<Album>) {
        println(albums)
    }

    override fun showErrorMessage(errorMessage: String) {
        println(errorMessage)
    }
}