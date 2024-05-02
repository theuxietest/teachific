package com.so.luotk.models.output

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MarkingCreteria (
    @SerializedName("correct_marks")
    @Expose
     val correctMarks: String? = null,

    @SerializedName("wrong_marks")
    @Expose
     val wrongMarks: String? = null,

    @SerializedName("unanswered_marks")
    @Expose
     val unansweredMarks: String? = null
)