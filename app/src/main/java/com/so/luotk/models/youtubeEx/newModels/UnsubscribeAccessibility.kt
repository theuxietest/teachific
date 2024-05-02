package com.so.luotk.models.youtubeEx.newModels

import com.google.gson.annotations.SerializedName

class UnsubscribeAccessibility {
    @SerializedName("accessibilityData")
    var accessibilityData: AccessibilityData? = null
    override fun toString(): String {
        return "UnsubscribeAccessibility{" +
                "accessibilityData = '" + accessibilityData + '\'' +
                "}"
    }
}