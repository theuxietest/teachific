package com.so.luotk.models.youtubeEx.newModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Popup : Parcelable, Serializable {
    @SerializedName("confirmDialogRenderer")
    var confirmDialogRenderer: ConfirmDialogRenderer? = null
    override fun toString(): String {
        return "Popup{" +
                "confirmDialogRenderer = '" + confirmDialogRenderer + '\'' +
                "}"
    }
}