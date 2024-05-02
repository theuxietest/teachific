package com.so.luotk.models.output;

import java.util.ArrayList;

import lombok.Data;

@Data
public class GetBatchListResponse {
    private ArrayList<BatchListResult> result;

    private String success;

    private String status;

    public ArrayList<BatchListResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<BatchListResult> result) {
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
