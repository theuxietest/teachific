package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class WebPlayerShareEntityServiceEndpoint : Parcelable, Serializable {
    @SerializedName("serializedShareEntity")
    var serializedShareEntity: String? = null
    override fun toString(): String {
        return "WebPlayerShareEntityServiceEndpoint{" +
                "serializedShareEntity = '" + serializedShareEntity + '\'' +
                "}"
    }
}