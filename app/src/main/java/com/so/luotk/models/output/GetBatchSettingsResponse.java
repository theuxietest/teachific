package com.so.luotk.models.output;

import lombok.Data;

@Data
public class GetBatchSettingsResponse {
    private GetBatchSettingsResult result;

    private String success;

    private String status;

    private Extra extra;

    public GetBatchSettingsResult getResult() {
        return result;
    }

    public void setResult(GetBatchSettingsResult result) {
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

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }
}
