package com.so.luotk.models.youtubeEx.newModels

import com.google.gson.annotations.SerializedName

class RunsItem {
    @SerializedName("text")
    var text: String? = null
    override fun toString(): String {
        return "RunsItem{" +
                "text = '" + text + '\'' +
                "}"
    }
}