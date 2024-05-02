package com.so.luotk.models.youtubeEx.newModels

import com.google.gson.annotations.SerializedName

class SignalServiceEndpoint {
    @SerializedName("signal")
    var signal: String? = null

    @SerializedName("actions")
    var actions: List<ActionsItem>? = null
    override fun toString(): String {
        return "SignalServiceEndpoint{" +
                "signal = '" + signal + '\'' +
                ",actions = '" + actions + '\'' +
                "}"
    }
}