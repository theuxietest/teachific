package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Watermark : Parcelable, Serializable {
    @SerializedName("thumbnails")
    var thumbnails: List<ThumbnailsItem>? = null
    override fun toString(): String {
        return "Watermark{" +
                "thumbnails = '" + thumbnails + '\'' +
                "}"
    }
}