package com.so.luotk.models.output;

import lombok.Data;

@Data
public class VerifyLoginResponse {
    private String success;
    private String status;
    private VerifyLoginResult result;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public VerifyLoginResult getResult() {
        return result;
    }

    public void setResult(VerifyLoginResult result) {
        this.result = result;
    }
}
