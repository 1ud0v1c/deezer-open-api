package com.ludovic.vimont.deezeropenapi.screens.detail

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ludovic.vimont.deezeropenapi.R
import com.ludovic.vimont.deezeropenapi.helper.IntentHelper
import com.ludovic.vimont.deezeropenapi.helper.ViewHelper
import com.ludovic.vimont.deezeropenapi.model.Track

/**
 * Display each track of the clicked album by the user thanks to DetailInteractor
 */
class TrackAdapter(private val tracks: ArrayList<Track>): RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    private var lastViewHolderClicked: TrackViewHolder? = null
    var onItemClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(
            itemView
        )
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track: Track = tracks[position]
        val context: Context = holder.itemView.context
        holder.textViewTrackTitle.text = track.title_short
        holder.imageViewShare.setOnClickListener {
            IntentHelper.shareLink(context, track.link, "See my last discover!")
        }
        holder.imageViewSeeOnWeb.setOnClickListener {
            IntentHelper.openWebPage(context, track.link)
        }
        holder.itemView.setOnClickListener {
            // If the previous viewHolder clicked is the same that before, we set him to passive state
            lastViewHolderClicked = if (lastViewHolderClicked == holder) {
                setPassiveState(holder)
                null
            } else {
                // Otherwise, we pass the last viewHolder to passive & update the current to active
                lastViewHolderClicked?.let { lastTrackViewHolder ->
                    setPassiveState(lastTrackViewHolder)
                }
                setActiveState(holder)
                holder
            }
            onItemClick?.invoke(track)
        }
    }

    /**
     * Set the default appearance state of a track
     */
    private fun setPassiveState(trackViewHolder: TrackViewHolder) {
        val context: Context = trackViewHolder.itemView.context

        val imageViewPlay: ImageView = trackViewHolder.imageViewPlay
        imageViewPlay.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause_to_play))
        launchAnimatedDrawable(imageViewPlay)

        val imageViewSeeOnWeb: ImageView = trackViewHolder.imageViewSeeOnWeb
        imageViewSeeOnWeb.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.on_web_passive))
        launchAnimatedDrawable(imageViewSeeOnWeb)

        val imageViewShare: ImageView = trackViewHolder.imageViewShare
        imageViewShare.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.share_passive))
        launchAnimatedDrawable(imageViewShare)

        val activeColor: Int = ViewHelper.getColor(context, R.color.secondaryColor)
        val passiveColor: Int = ViewHelper.getColor(context, R.color.whiteColor)
        ViewHelper.textColorAnimation(trackViewHolder.textViewTrackTitle, activeColor, passiveColor, 300)
    }

    /**
     * Set the trackViewHolder state to active, to display current selected music
     */
    private fun setActiveState(trackViewHolder: TrackViewHolder) {
        val context: Context = trackViewHolder.itemView.context

        val imageViewPlay: ImageView = trackViewHolder.imageViewPlay
        imageViewPlay.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_to_pause))
        launchAnimatedDrawable(imageViewPlay)

        val imageViewSeeOnWeb: ImageView = trackViewHolder.imageViewSeeOnWeb
        imageViewSeeOnWeb.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.on_web_active))
        launchAnimatedDrawable(imageViewSeeOnWeb)

        val imageViewShare: ImageView = trackViewHolder.imageViewShare
        imageViewShare.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.share_active))
        launchAnimatedDrawable(imageViewShare)

        val activeColor: Int = ViewHelper.getColor(context, R.color.secondaryColor)
        val passiveColor: Int = ViewHelper.getColor(context, R.color.whiteColor)
        ViewHelper.textColorAnimation(trackViewHolder.textViewTrackTitle, passiveColor, activeColor, 300)
    }

    /**
     * Launch an imageView animated vector drawable
     */
    private fun launchAnimatedDrawable(imageView: ImageView) {
        val drawable: Drawable = imageView.drawable
        if (drawable is Animatable) {
            val animatable: Animatable = drawable
            if (!animatable.isRunning) {
                animatable.start()
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun getTracks(): List<Track> {
        return tracks
    }

    fun addItems(items: List<Track>) {
        tracks.addAll(items)
        notifyDataSetChanged()
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewPlay: ImageView = itemView.findViewById(R.id.image_view_play)
        val imageViewShare: ImageView = itemView.findViewById(R.id.image_view_share)
        val imageViewSeeOnWeb: ImageView = itemView.findViewById(R.id.image_view_see_on_web)
        val textViewTrackTitle: TextView = itemView.findViewById(R.id.text_view_track_title)
    }
}