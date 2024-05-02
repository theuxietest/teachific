
package com.so.luotk.models.newmodels.otpModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("userDetail")
    @Expose
    private UserDetail userDetail;
    @SerializedName("batch")
    @Expose
    private String batch;
    @SerializedName("live_option")
    @Expose
    private Integer liveOption;
    @SerializedName("live_url")
    @Expose
    private String liveUrl;
    @SerializedName("fcm_key")
    @Expose
    private String fcmKey;
    @SerializedName("type")
    @Expose
    private String type;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Integer getLiveOption() {
        return liveOption;
    }

    public void setLiveOption(Integer liveOption) {
        this.liveOption = liveOption;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public String getFcmKey() {
        return fcmKey;
    }

    public void setFcmKey(String fcmKey) {
        this.fcmKey = fcmKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
