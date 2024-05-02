package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class ButtonText : Parcelable, Serializable {
    @SerializedName("runs")
    var runs: List<RunsItem>? = null
    override fun toString(): String {
        return "ButtonText{" +
                "runs = '" + runs + '\'' +
                "}"
    }
}