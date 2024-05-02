package com.so.luotk.models.output;

import lombok.Data;

@Data
public class GetReportTestListResponse {
    private GetReportTestListResult result;

    private String success;

    private String status;

    public GetReportTestListResult getResult() {
        return result;
    }

    public void setResult(GetReportTestListResult result) {
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
