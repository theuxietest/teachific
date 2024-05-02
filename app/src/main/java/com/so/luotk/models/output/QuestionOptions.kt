package com.so.luotk.models.output

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class QuestionOptions(

        @SerializedName("0")
        @Expose
        var option1: OptionModel? = null,
        @SerializedName("1")
        @Expose
        var option2: OptionModel? = null,
        @SerializedName("2")
        @Expose
        var option3: OptionModel? = null,
        @SerializedName("3")
        @Expose
        var option4: OptionModel? = null,
        @SerializedName("correct_option")
        @Expose
        var correct_option: String? = null

)