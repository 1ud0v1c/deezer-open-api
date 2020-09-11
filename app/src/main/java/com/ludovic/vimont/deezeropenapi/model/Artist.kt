package com.ludovic.vimont.deezeropenapi.model

import android.os.Parcel
import android.os.Parcelable

data class Artist(
    val id: Int,
    val name: String,
    val picture: String,
    val picture_small: String,
    val picture_medium: String,
    val picture_big: String,
    val picture_xl: String,
    val tracklist: String
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: "",
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(name)
        writeString(picture)
        writeString(picture_small)
        writeString(picture_medium)
        writeString(picture_big)
        writeString(picture_xl)
        writeString(tracklist)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Artist> = object : Parcelable.Creator<Artist> {
            override fun createFromParcel(source: Parcel): Artist = Artist(source)
            override fun newArray(size: Int): Array<Artist?> = arrayOfNulls(size)
        }
    }
}