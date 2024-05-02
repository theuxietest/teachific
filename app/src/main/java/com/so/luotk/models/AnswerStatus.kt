package com.so.luotk.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AnswerStatus(
        @SerializedName("fk_questionId")
        @Expose
        var fk_questionId: Int = -1,
        @SerializedName("status")
        @Expose
        var status: Int = 0,
        @SerializedName("time_taken")
        @Expose
        var time_taken: Int = 0,
        @SerializedName("option")
        @Expose
        var option: String = "-1",
        @SerializedName("fk_answerId")
        @Expose
        var fk_answerId: Int = -1



)
