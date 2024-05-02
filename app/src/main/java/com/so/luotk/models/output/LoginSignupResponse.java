package com.so.luotk.models.output;

import lombok.Data;

@Data
public class  LoginSignupResponse {
    private String success;

    private String status;

    private Result result;

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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
