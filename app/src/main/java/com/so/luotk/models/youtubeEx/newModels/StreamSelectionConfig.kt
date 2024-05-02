package com.so.luotk.models.youtubeEx.newModels

import com.google.gson.annotations.SerializedName

class StreamSelectionConfig {
    @SerializedName("maxBitrate")
    var maxBitrate: String? = null
    override fun toString(): String {
        return "StreamSelectionConfig{" +
                "maxBitrate = '" + maxBitrate + '\'' +
                "}"
    }
}