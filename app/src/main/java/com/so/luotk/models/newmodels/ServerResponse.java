package com.so.luotk.models.newmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ServerResponse {
    @SerializedName("success")
    @Expose
    Boolean success;
    @SerializedName("status")
    @Expose
    Integer status;
    @SerializedName("result")
    @Expose
    String result;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
