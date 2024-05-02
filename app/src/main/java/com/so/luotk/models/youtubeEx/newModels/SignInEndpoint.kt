package com.so.luotk.models.youtubeEx.newModels

import com.google.gson.annotations.SerializedName

class SignInEndpoint {
    @SerializedName("commandMetadata")
    var commandMetadata: CommandMetadata? = null

    @SerializedName("clickTrackingParams")
    var clickTrackingParams: String? = null
    override fun toString(): String {
        return "SignInEndpoint{" +
                "commandMetadata = '" + commandMetadata + '\'' +
                ",clickTrackingParams = '" + clickTrackingParams + '\'' +
                "}"
    }
}