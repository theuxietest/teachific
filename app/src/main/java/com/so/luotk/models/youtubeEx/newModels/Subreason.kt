package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Subreason : Parcelable {
    @SerializedName("simpleText")
    var simpleText: String? = null
    override fun toString(): String {
        return "Subreason{" +
                "simpleText = '" + simpleText + '\'' +
                "}"
    }
}