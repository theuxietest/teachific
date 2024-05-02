package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class QoeUrl : Parcelable, Serializable {
    @SerializedName("baseUrl")
    var baseUrl: String? = null
    override fun toString(): String {
        return "QoeUrl{" +
                "baseUrl = '" + baseUrl + '\'' +
                "}"
    }
}