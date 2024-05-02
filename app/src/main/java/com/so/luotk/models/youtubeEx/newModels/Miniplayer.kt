package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Miniplayer : Parcelable, Serializable {
    @SerializedName("miniplayerRenderer")
    var miniplayerRenderer: MiniplayerRenderer? = null
    override fun toString(): String {
        return "Miniplayer{" +
                "miniplayerRenderer = '" + miniplayerRenderer + '\'' +
                "}"
    }
}