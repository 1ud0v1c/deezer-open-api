package com.ludovic.vimont.deezeropenapi.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.ludovic.vimont.deezeropenapi.R
import com.ludovic.vimont.deezeropenapi.databinding.ActivityMainBinding
import com.ludovic.vimont.deezeropenapi.model.Album
import com.ludovic.vimont.deezeropenapi.ui.GridSpacingItemDecoration

/**
 * Entry point of the Application which will list of available albums of an user.
 */
class HomeActivity : AppCompatActivity(), HomeContract.View {
    companion object {
        const val GRID_SPAN_COUNT = 2
    }
    private val homePresenter = HomePresenter(this, HomeInteractor())

    private val albumAdapter = HomeAlbumAdapter(ArrayList())
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val gridLayoutManager = GridLayoutManager(applicationContext, GRID_SPAN_COUNT)
        mainBinding.recyclerView.adapter = albumAdapter
        mainBinding.recyclerView.layoutManager = gridLayoutManager
        val gridSpaceDimension: Int = resources.getDimension(R.dimen.album_grid_space).toInt()
        mainBinding.recyclerView.addItemDecoration(GridSpacingItemDecoration(GRID_SPAN_COUNT, gridSpaceDimension, false))
        
        homePresenter.start()
    }

    override fun setAlbums(albums: List<Album>) {
        albumAdapter.addItems(albums)
    }

    override fun showErrorMessage(errorMessage: String) {
        println(errorMessage)
    }
}