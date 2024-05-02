package com.so.luotk.models.newmodels.loginModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Result {
    @SerializedName("user_id")
    @Expose
    public Integer userId;
    @SerializedName("otp")
    @Expose
    public Integer otp;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }
}
