package com.so.luotk.models.output;

import lombok.Data;

@Data
public class GetBatchLiveRoomResponse {
    private GetBatchLiveRoomResult result;

    private String success;

    private String status;

    public GetBatchLiveRoomResult getResult() {
        return result;
    }

    public void setResult(GetBatchLiveRoomResult result) {
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
