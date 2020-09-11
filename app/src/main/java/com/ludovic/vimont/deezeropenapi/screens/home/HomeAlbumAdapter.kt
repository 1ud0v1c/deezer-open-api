package com.ludovic.vimont.deezeropenapi.screens.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.ludovic.vimont.deezeropenapi.R
import com.ludovic.vimont.deezeropenapi.model.Album

/**
 * Adapter used to display result item received thanks to HomeInteractor
 */
class HomeAlbumAdapter(private val albums: ArrayList<Album>): RecyclerView.Adapter<HomeAlbumAdapter.AlbumViewHolder>() {
    companion object {
        const val FADE_IN_DURATION = 300
    }
    var onItemClick: ((Album) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(
            itemView
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album: Album = albums[position]
        val context: Context = holder.itemView.context

        val cornersRadiusSize: Int = context.resources.getDimension(R.dimen.album_cover_rounded_corners).toInt()
        Glide.with(context)
            .load(album.cover_medium)
            .placeholder(R.drawable.album_default_cover)
            .transition(DrawableTransitionOptions.withCrossFade(FADE_IN_DURATION))
            .apply(RequestOptions.bitmapTransform(RoundedCorners(cornersRadiusSize)))
            .into(holder.imageViewCover)

        holder.textViewAlbumTitle.text = album.title
        holder.textViewAlbumArtistName.text = album.artist?.name
        val pluralsNumberOfTrackId: Int = R.plurals.home_activity_cover_album_number_of_track
        holder.textViewNumberOfTracks.text = context.resources.getQuantityString(pluralsNumberOfTrackId, album.nb_tracks, album.nb_tracks)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(album)
        }
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    fun addItems(items: List<Album>) {
        albums.addAll(items)
        notifyDataSetChanged()
    }

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewCover: ImageView = itemView.findViewById(R.id.image_view_cover)
        val textViewAlbumTitle: TextView = itemView.findViewById(R.id.text_view_album_title)
        val textViewAlbumArtistName: TextView = itemView.findViewById(R.id.text_view_album_artist_name)
        val textViewNumberOfTracks: TextView = itemView.findViewById(R.id.text_view_number_of_tracks)
    }
}