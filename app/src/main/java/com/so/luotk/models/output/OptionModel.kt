package com.so.luotk.models.output

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OptionModel(
        @SerializedName("answerId")
        @Expose
        var answerId: Int = -1,

        @SerializedName("answer")
        @Expose
        var answer: String? = null,

        @SerializedName("is_correct")
        @Expose
        var fib_answ: String = "0"
) {
//    var fib_answer: String = ""


}
