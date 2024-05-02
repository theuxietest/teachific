package com.so.luotk.models.output

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TestSection (
    @SerializedName("id")
    @Expose
    val id: Int? = null,

    @SerializedName("section_name")
    @Expose
    val sectionName: String? = null,

    @SerializedName("section_instruction")
    @Expose
    val sectionInstruction: String? = null,

    @SerializedName("question_count")
    @Expose
    var questionCount: Int=0,

    @SerializedName("questions")
    @Expose
    val questions: List<TestQuestion>? = null
)