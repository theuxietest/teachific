package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class ActionsItem : Parcelable, Serializable {
    @SerializedName("openPopupAction")
    var openPopupAction: OpenPopupAction? = null

    @SerializedName("clickTrackingParams")
    var clickTrackingParams: String? = null
    override fun toString(): String {
        return "ActionsItem{" +
                "openPopupAction = '" + openPopupAction + '\'' +
                ",clickTrackingParams = '" + clickTrackingParams + '\'' +
                "}"
    }
}