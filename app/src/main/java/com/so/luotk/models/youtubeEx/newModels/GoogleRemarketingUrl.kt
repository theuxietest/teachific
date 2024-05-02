package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class GoogleRemarketingUrl : Parcelable, Serializable {
    @SerializedName("baseUrl")
    var baseUrl: String? = null

    @SerializedName("elapsedMediaTimeSeconds")
    var elapsedMediaTimeSeconds = 0
    override fun toString(): String {
        return "GoogleRemarketingUrl{" +
                "baseUrl = '" + baseUrl + '\'' +
                ",elapsedMediaTimeSeconds = '" + elapsedMediaTimeSeconds + '\'' +
                "}"
    }
}