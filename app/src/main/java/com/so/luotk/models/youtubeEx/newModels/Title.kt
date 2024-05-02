package com.so.luotk.models.youtubeEx.newModels

import com.google.gson.annotations.SerializedName

class Title {
    @SerializedName("simpleText")
    var simpleText: String? = null
    override fun toString(): String {
        return "Title{" +
                "simpleText = '" + simpleText + '\'' +
                "}"
    }
}