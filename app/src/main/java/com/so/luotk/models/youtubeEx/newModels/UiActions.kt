package com.so.luotk.models.youtubeEx.newModels

import com.google.gson.annotations.SerializedName

class UiActions {
    @SerializedName("hideEnclosingContainer")
    var isHideEnclosingContainer = false
    override fun toString(): String {
        return "UiActions{" +
                "hideEnclosingContainer = '" + isHideEnclosingContainer + '\'' +
                "}"
    }
}