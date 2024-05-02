package com.so.luotk.models.youtubeEx.newModels

import com.google.gson.annotations.SerializedName

class ServiceTrackingParamsItem {
    @SerializedName("service")
    var service: String? = null

    @SerializedName("params")
    var params: List<ParamsItem>? = null
    override fun toString(): String {
        return "ServiceTrackingParamsItem{" +
                "service = '" + service + '\'' +
                ",params = '" + params + '\'' +
                "}"
    }
}