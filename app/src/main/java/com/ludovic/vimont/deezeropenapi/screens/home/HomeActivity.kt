package com.ludovic.vimont.deezeropenapi.screens.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ludovic.vimont.deezeropenapi.R
import com.ludovic.vimont.deezeropenapi.databinding.ActivityMainBinding
import com.ludovic.vimont.deezeropenapi.model.Album
import com.ludovic.vimont.deezeropenapi.screens.detail.DetailActivity
import com.ludovic.vimont.deezeropenapi.ui.EndlessRecyclerViewScrollListener
import com.ludovic.vimont.deezeropenapi.ui.GridSpacingItemDecoration

/**
 * Entry point of the Application which will list of available albums of an user.
 */
class HomeActivity : AppCompatActivity(), HomeContract.View {
    companion object {
        const val KEY_INTENT_ALBUM_EXTRA = "home_activity_album_intent_key"
        const val GRID_SPAN_COUNT = 2
    }
    private val homePresenter = HomePresenter(this, HomeInteractor())

    private val albumAdapter = HomeAlbumAdapter(ArrayList())
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val gridLayoutManager = GridLayoutManager(applicationContext, GRID_SPAN_COUNT)
        val recyclerViewAlbums: RecyclerView = mainBinding.recyclerViewAlbums
        recyclerViewAlbums.adapter = albumAdapter
        recyclerViewAlbums.layoutManager = gridLayoutManager
        val gridSpaceDimension: Int = resources.getDimension(R.dimen.album_grid_space).toInt()
        recyclerViewAlbums.addItemDecoration(GridSpacingItemDecoration(GRID_SPAN_COUNT, gridSpaceDimension, false))

        endlessRecyclerViewScrollListener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(
                currentPage: Int,
                totalItemsCount: Int,
                view: RecyclerView?) {
                homePresenter.start(applicationContext, currentPage)
            }
        }
        recyclerViewAlbums.addOnScrollListener(endlessRecyclerViewScrollListener)
        albumAdapter.onItemClick = { album ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(KEY_INTENT_ALBUM_EXTRA, album)
            startActivity(intent)
        }

        homePresenter.start(applicationContext)
    }

    override fun setAlbums(albums: List<Album>) {
        albumAdapter.addItems(albums)
    }

    override fun showErrorMessage(errorMessage: String) {
        val snackBar: Snackbar = Snackbar.make(mainBinding.root,
            errorMessage, Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction(getString(R.string.action_retry)) {
            homePresenter.start(applicationContext, endlessRecyclerViewScrollListener.getCurrentPage())
            snackBar.dismiss()
        }
        val snackBarView: View = snackBar.view
        val snackBarTextView: TextView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text)
        snackBarTextView.maxLines = 3
        snackBar.show()
    }
}