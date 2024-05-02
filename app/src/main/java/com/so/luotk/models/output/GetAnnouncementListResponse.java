package com.so.luotk.models.output;

import lombok.Data;

@Data
public class GetAnnouncementListResponse {
    private GetAnnouncementListResult result;

    private boolean success;

    private int status;

    public GetAnnouncementListResult getResult() {
        return result;
    }

    public void setResult(GetAnnouncementListResult result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
