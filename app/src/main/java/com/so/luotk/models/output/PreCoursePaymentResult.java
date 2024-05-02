package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class PreCoursePaymentResult implements Serializable {
    @SerializedName("responseSuccessURL")
    @Expose
    private String responseSuccessURL;
    @SerializedName("userEmail")
    @Expose
    private String userEmail;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("test_secret")
    @Expose
    private String testSecret;
    @SerializedName("live_secret")
    @Expose
    private String liveSecret;
    @SerializedName("is_live")
    @Expose
    private Integer isLive;
    @SerializedName("courseId")
    @Expose
    private Integer courseId;

    public String getResponseSuccessURL() {
        return responseSuccessURL;
    }

    public void setResponseSuccessURL(String responseSuccessURL) {
        this.responseSuccessURL = responseSuccessURL;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTestSecret() {
        return testSecret;
    }

    public void setTestSecret(String testSecret) {
        this.testSecret = testSecret;
    }

    public String getLiveSecret() {
        return liveSecret;
    }

    public void setLiveSecret(String liveSecret) {
        this.liveSecret = liveSecret;
    }

    public Integer getIsLive() {
        return isLive;
    }

    public void setIsLive(Integer isLive) {
        this.isLive = isLive;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }
}
