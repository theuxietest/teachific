package com.so.luotk.models.output

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class KtServerResponse (

    @SerializedName("success")
    @Expose
    var success: Boolean=false,

    @SerializedName("status")
    @Expose
    var status: Int=-1,

    @SerializedName("result")
    @Expose
    var result: String? = null

)