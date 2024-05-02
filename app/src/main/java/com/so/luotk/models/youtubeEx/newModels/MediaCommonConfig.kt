package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class MediaCommonConfig : Parcelable, Serializable {
    @SerializedName("dynamicReadaheadConfig")
    var dynamicReadaheadConfig: DynamicReadaheadConfig? = null
    override fun toString(): String {
        return "MediaCommonConfig{" +
                "dynamicReadaheadConfig = '" + dynamicReadaheadConfig + '\'' +
                "}"
    }
}