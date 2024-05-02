package com.so.luotk.models.output;

import lombok.Data;

@Data
public class VerifyOrgResponse {
    private VerifyOrgResult result;

    private String success;

    private String status;

    public VerifyOrgResult getResult() {
        return result;
    }

    public void setResult(VerifyOrgResult result) {
        this.result = result;
    }

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
}
