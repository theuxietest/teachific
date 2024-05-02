package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class AccessibilityData : Parcelable, Serializable {
    @SerializedName("label")
    var label: String? = null
    override fun toString(): String {
        return "AccessibilityData{" +
                "label = '" + label + '\'' +
                "}"
    }
}