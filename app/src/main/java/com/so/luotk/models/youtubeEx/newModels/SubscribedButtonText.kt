package com.so.luotk.models.youtubeEx.newModels

import com.google.gson.annotations.SerializedName

class SubscribedButtonText {
    @SerializedName("runs")
    var runs: List<RunsItem>? = null
    override fun toString(): String {
        return "SubscribedButtonText{" +
                "runs = '" + runs + '\'' +
                "}"
    }
}