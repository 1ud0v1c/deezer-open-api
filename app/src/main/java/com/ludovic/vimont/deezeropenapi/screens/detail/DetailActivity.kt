package com.ludovic.vimont.deezeropenapi.screens.detail

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
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
import com.ludovic.vimont.deezeropenapi.model.Artist
import com.ludovic.vimont.deezeropenapi.model.Track
import com.ludovic.vimont.deezeropenapi.player.AudioSessionCallback
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
    private lateinit var detailMediaSessionHandler: DetailMediaSessionHandler

    private var mAlbum: Album? = null
    private var albumCoverBitmap: Bitmap? = BitmapHelper.emptyBitmap()

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
            mAlbum = album
        }

        detailMediaSessionHandler = DetailMediaSessionHandler(this)
    }

    private fun updateAlbumInformation(album: Album) {
        Glide.with(applicationContext)
            .asBitmap()
            .load(album.cover_medium)
            .placeholder(R.drawable.album_default_cover)
            .transition(BitmapTransitionOptions.withCrossFade(FADE_IN_DURATION))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    detailBinding.imageViewAlbumCover.setImageBitmap(resource)
                    albumCoverBitmap = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    albumCoverBitmap = null
                }
            })

        detailBinding.textViewAlbumTitle.text = album.title

        updateAlbumArtist(album)

        trackAdapter.onItemClick = { clickedPosition, playingNeeded ->
            detailMediaSessionHandler.askMediaSessionToSetCurrentTrack(clickedPosition, playingNeeded)
        }
    }

    private fun updateAlbumArtist(album: Album) {
        val artist: Artist = album.artist ?: return

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
            .placeholder(R.drawable.artist_default_profile)
            .transition(DrawableTransitionOptions.withCrossFade(FADE_IN_DURATION))
            .into(detailBinding.imageViewArtistProfile)

        val artistName: String = artist.name
        detailBinding.textViewArtistName.text = artistName
        val wikipediaPage: String = resources.getString(
            R.string.detail_activity_artist_wikipedia,
            artistName.replace(' ', '_')
        )
        detailBinding.textViewArtistWikipediaPage.text = wikipediaPage
    }

    /**
     * We receive the event from the API call and adapt the UI in consequence
     */
    override fun setTracks(tracks: List<Track>) {
        trackAdapter.addItems(tracks)
        val albumDuration: String = computeAlbumDuration(tracks)
        val textDuration: String = resources.getString(R.string.detail_activity_album_duration, albumDuration, tracks.size)
        detailBinding.textViewAlbumAdditionalInformation.text = textDuration
        MediaControllerCompat.getMediaController(this)?.let { mediaController ->
            computeQueue(mediaController)
        }
    }

    /**
     * Build the MediaSession play queue based on the receive tracks
     */
    fun computeQueue(mediaController: MediaControllerCompat) {
        val bundle = Bundle()
        val queue = ArrayList<MediaSessionCompat.QueueItem>()
        trackAdapter.getTracks().forEachIndexed { index, track ->
            mAlbum?.let {
                AudioHelper.buildMediaDescriptionFromTrack(it, track, albumCoverBitmap).let { mediaDescriptionCompat ->
                    queue.add(MediaSessionCompat.QueueItem(mediaDescriptionCompat, index.toLong()))
                }
            }
        }
        bundle.putParcelableArrayList(AudioSessionCallback.KEY_PLAYLIST, queue)
        mediaController.transportControls.sendCustomAction(
            AudioSessionCallback.MEDIA_SESSION_ACTION_SET_PLAYLIST, bundle
        )
    }

    /**
     * Used to adapt UI based on notification click
     */
    fun updateAdapterViewHolderState(position: Int) {
        detailBinding.recyclerViewTracks.findViewHolderForLayoutPosition(position)?.let { viewHolder ->
            if (viewHolder is DetailTrackAdapter.TrackViewHolder) {
                trackAdapter.updateViewHolderState(viewHolder)
            }
        }
    }

    /**
     * Calculate the album duration and return a format display
     */
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