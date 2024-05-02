package com.so.luotk.models.output;

import java.util.ArrayList;

import lombok.Data;

@Data
public class GetBatchOverviewResponse {
    private ArrayList<GetBatchOverviewResult> result;

    private String success;

    private Extra extra;

    private String status;

    public ArrayList<GetBatchOverviewResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<GetBatchOverviewResult> result) {
        this.result = result;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
