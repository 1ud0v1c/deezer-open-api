package com.ludovic.vimont.deezeropenapi.screens.detail

import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.ludovic.vimont.deezeropenapi.R
import com.ludovic.vimont.deezeropenapi.databinding.ActivityDetailBinding
import com.ludovic.vimont.deezeropenapi.helper.AudioHelper
import com.ludovic.vimont.deezeropenapi.helper.BitmapHelper
import com.ludovic.vimont.deezeropenapi.helper.ViewHelper
import com.ludovic.vimont.deezeropenapi.model.Album
import com.ludovic.vimont.deezeropenapi.model.Track
import com.ludovic.vimont.deezeropenapi.player.MediaPlaybackService
import com.ludovic.vimont.deezeropenapi.screens.home.HomeActivity
import com.ludovic.vimont.deezeropenapi.ui.MarginItemDecoration

class DetailActivity : AppCompatActivity(), DetailContract.View {
    companion object {
        const val FADE_IN_DURATION = 500
        const val ALBUM_DURATION_FORMAT = "%02d min %02d s"
    }
    private val detailPresenter = DetailPresenter(this, DetailInteractor())

    private val trackAdapter = DetailTrackAdapter(ArrayList())
    private lateinit var detailBinding: ActivityDetailBinding
    private lateinit var mediaBrowser: MediaBrowserCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        // We need this to have compatible animated vector drawable for Android < API 21
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        val linearLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        detailBinding.recyclerViewTracks.adapter = trackAdapter
        detailBinding.recyclerViewTracks.layoutManager = linearLayoutManager
        val marginSize: Int = resources.getDimension(R.dimen.detail_activity_track_adapter_item_decoration_margin).toInt()
        detailBinding.recyclerViewTracks.addItemDecoration(MarginItemDecoration(marginSize))

        val nullableAlbum: Album? = intent.extras?.getParcelable(HomeActivity.KEY_INTENT_ALBUM_EXTRA)
        nullableAlbum?.let { album ->
            updateAlbumInformation(album)
            detailPresenter.start(applicationContext, album.getId())
        }

        val componentName = ComponentName(applicationContext, MediaPlaybackService::class.java)
        mediaBrowser = MediaBrowserCompat(applicationContext, componentName, connectionCallbacks, null)
    }

    private val connectionCallbacks: MediaBrowserCompat.ConnectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaBrowser.sessionToken.also { token ->
                val mediaController = MediaControllerCompat(applicationContext, token)
                MediaControllerCompat.setMediaController(this@DetailActivity, mediaController)
            }
            buildTransportControls()
        }

        override fun onConnectionSuspended() {
            println("onConnectionSuspended, the Service has crashed.")
        }

        override fun onConnectionFailed() {
            println("onConnectionFailed, the Service has refused our connection.")
        }
    }

    fun buildTransportControls() {
        val mediaController: MediaControllerCompat = MediaControllerCompat.getMediaController(this@DetailActivity)
        mediaController.registerCallback(controllerCallback)
    }

    private var controllerCallback: MediaControllerCompat.Callback = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            println("onMetadataChanged")
            println(metadata?.mediaMetadata.toString())
            println(metadata?.description)
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            println("onPlaybackStateChanged")
            state?.let { playbackState ->
                when (playbackState.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        println("playing")
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        println("paused")
                    }
                    else -> {
                        println(state)
                    }
                }
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    public override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    public override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
    }

    private fun updateAlbumInformation(album: Album) {
        var bitmap: Bitmap? = BitmapHelper.emptyBitmap()

        Glide.with(applicationContext)
            .asBitmap()
            .load(album.cover_medium)
            .placeholder(R.drawable.album_default_cover)
            .transition(BitmapTransitionOptions.withCrossFade(FADE_IN_DURATION))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    detailBinding.imageViewAlbumCover.setImageBitmap(resource)
                    bitmap = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    bitmap = null
                }
            })

        detailBinding.textViewAlbumTitle.text = album.title

        album.artist?.let { artist ->
            val albumByText: String = resources.getString(
                R.string.detail_activity_album_information,
                artist.name,
                album.getReleaseDate()
            )
            ViewHelper.highlightTextViewWithColor(
                detailBinding.textViewAlbumBy,
                albumByText,
                artist.name,
                ViewHelper.getColor(applicationContext, R.color.secondaryColor)
            )

            Glide.with(applicationContext)
                .load(artist.picture_medium)
                .transition(DrawableTransitionOptions.withCrossFade(FADE_IN_DURATION))
                .into(detailBinding.imageViewArtistProfile)

            val artistName: String = artist.name
            detailBinding.textViewArtistName.text = artistName
            val wikipediaPage: String = resources.getString(
                R.string.detail_activity_artist_wikipedia,
                artistName.replace(" ", "_")
            )
            detailBinding.textViewArtistWikipediaPage.text = wikipediaPage

            trackAdapter.onItemClick = { currentTrack, playingNeeded ->
                val mediaController: MediaControllerCompat = MediaControllerCompat.getMediaController(this@DetailActivity)
                AudioHelper.buildMediaDescriptionFromTrack(album, currentTrack, bitmap).let {
                    mediaController.addQueueItem(it)
                    if (playingNeeded) {
                        mediaController.transportControls.play()
                    } else {
                        mediaController.transportControls.pause()
                    }
                }
            }
        }
    }

    override fun setTracks(tracks: List<Track>) {
        trackAdapter.addItems(tracks)
        val albumDuration: String = computeAlbumDuration(tracks)
        val textDuration: String = resources.getString(R.string.detail_activity_album_duration, albumDuration, tracks.size)
        detailBinding.textViewAlbumAdditionalInformation.text = textDuration
    }

    private fun computeAlbumDuration(tracks: List<Track>): String {
        var totalDurationInSec = 0
        for (track: Track in tracks) {
            totalDurationInSec += track.duration
        }
        val minutes: Int = (totalDurationInSec / 60)
        val seconds: Int = totalDurationInSec % 60
        return String.format(ALBUM_DURATION_FORMAT, minutes, seconds)
    }

    override fun showErrorMessage(errorMessage: String) {
        val snackBar: Snackbar = Snackbar.make(detailBinding.root,
            errorMessage, Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction(getString(R.string.ok_action)) {
            snackBar.dismiss()
        }
        val snackBarView: View = snackBar.view
        val snackBarTextView: TextView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text)
        snackBarTextView.maxLines = 3
        snackBar.show()
    }
}