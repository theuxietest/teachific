package com.so.luotk.models.output

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.so.luotk.models.AnswerStatus

data class TestQuestion(
        @SerializedName("id")
        @Expose
        var id: Int? = null,

        @SerializedName("question")
        @Expose
        var question: String? = null,

        @SerializedName("type")
        @Expose
        var type: Int? = null,

        @SerializedName("level")
        @Expose
        var level: Int? = null,

        @SerializedName("solution")
        @Expose
        var solution: String? = null,

        @SerializedName("options")
        @Expose
        var options: QuestionOptions? = null,

        @SerializedName("markingCreteria")
        @Expose
        var markingCreteria: MarkingCreteria? = null,

        @SerializedName("answer_status")
        @Expose
        var answerStatus: AnswerStatus? = null,
        @SerializedName("quesStat")
        @Expose
        var quesStat: Int = 0




)