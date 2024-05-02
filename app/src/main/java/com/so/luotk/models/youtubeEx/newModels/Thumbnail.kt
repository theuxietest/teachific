package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Thumbnail : Parcelable {
    @SerializedName("thumbnails")
    var thumbnails: List<ThumbnailsItem>? = null
    override fun toString(): String {
        return "Thumbnail{" +
                "thumbnails = '" + thumbnails + '\'' +
                "}"
    }
}