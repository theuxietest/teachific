package com.so.luotk.models.output

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class ObjectiveTestDetailsResponse(
        @SerializedName("success")
        @Expose
        var success: Boolean? = null,


        @SerializedName("status")
        @Expose
        var status: Int? = null,


        @SerializedName("result")
        @Expose
        var result: ObjecticeTestDetailsResult? = null


)
