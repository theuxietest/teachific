package com.so.luotk.models.youtubeEx.newModels

import com.google.gson.annotations.SerializedName

class SubscribeButton {
    @SerializedName("subscribeButtonRenderer")
    var subscribeButtonRenderer: SubscribeButtonRenderer? = null
    override fun toString(): String {
        return "SubscribeButton{" +
                "subscribeButtonRenderer = '" + subscribeButtonRenderer + '\'' +
                "}"
    }
}