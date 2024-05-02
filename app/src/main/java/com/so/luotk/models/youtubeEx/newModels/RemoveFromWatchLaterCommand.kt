package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class RemoveFromWatchLaterCommand : Parcelable, Serializable {
    @SerializedName("commandMetadata")
    var commandMetadata: CommandMetadata? = null

    @SerializedName("playlistEditEndpoint")
    var playlistEditEndpoint: PlaylistEditEndpoint? = null

    @SerializedName("clickTrackingParams")
    var clickTrackingParams: String? = null
    override fun toString(): String {
        return "RemoveFromWatchLaterCommand{" +
                "commandMetadata = '" + commandMetadata + '\'' +
                ",playlistEditEndpoint = '" + playlistEditEndpoint + '\'' +
                ",clickTrackingParams = '" + clickTrackingParams + '\'' +
                "}"
    }
}