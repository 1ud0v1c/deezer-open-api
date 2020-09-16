package com.ludovic.vimont.deezeropenapi.model

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

data class Album(
    private val id: Int,
    val title: String,
    val link: String,
    val cover: String,
    val cover_small: String,
    val cover_medium: String,
    val cover_big: String,
    val cover_xl: String,
    val nb_tracks: Int,
    val release_date: String,
    val record_type: String,
    val alternative: Album? = null,
    val tracklist: String,
    val artist: Artist? = null,
    val explicit_lyrics: Boolean,
    val time_add: Int
) : Parcelable {
    companion object {
        const val DEEZER_RELEASE_DATE_FORMAT = "yyyy-MM-dd"
        const val DESIRE_RELEASE_DATE_FORMAT = "dd/MM/yyy"

        @JvmField
        val CREATOR: Parcelable.Creator<Album> = object : Parcelable.Creator<Album> {
            override fun createFromParcel(source: Parcel): Album = Album(source)
            override fun newArray(size: Int): Array<Album?> = arrayOfNulls(size)
        }
    }
    /**
     * If we have alternative information, we should prefer this version than ou current album.
     */
    fun getId(): Int {
        if (alternative != null) {
            return alternative.id
        }
        return id
    }

    /**
     * A simple tool to be able to change the date format as we want.
     */
    fun getReleaseDate(desiredReleaseDateFormat: String = DESIRE_RELEASE_DATE_FORMAT): String {
        val formatDate: Date? = SimpleDateFormat(DEEZER_RELEASE_DATE_FORMAT, Locale.getDefault()).parse(release_date)
        formatDate?.let { date ->
            val desiredFormat = SimpleDateFormat(desiredReleaseDateFormat, Locale.getDefault())
            return desiredFormat.format(date)
        }
        return release_date
    }

    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readInt(),
        source.readString() ?: "",
        source.readString() ?: "",
        source.readParcelable<Album>(Album::class.java.classLoader),
        source.readString() ?: "",
        source.readParcelable<Artist>(Artist::class.java.classLoader),
        1 == source.readInt(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(title)
        writeString(link)
        writeString(cover)
        writeString(cover_small)
        writeString(cover_medium)
        writeString(cover_big)
        writeString(cover_xl)
        writeInt(nb_tracks)
        writeString(release_date)
        writeString(record_type)
        writeParcelable(alternative, 0)
        writeString(tracklist)
        writeParcelable(artist, 0)
        writeInt((if (explicit_lyrics) 1 else 0))
        writeInt(time_add)
    }
}