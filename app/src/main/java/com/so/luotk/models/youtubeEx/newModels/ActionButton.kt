package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class ActionButton : Parcelable, Serializable {
    @SerializedName("buttonRenderer")
    var buttonRenderer: ButtonRenderer? = null
    override fun toString(): String {
        return "ActionButton{" +
                "buttonRenderer = '" + buttonRenderer + '\'' +
                "}"
    }
}