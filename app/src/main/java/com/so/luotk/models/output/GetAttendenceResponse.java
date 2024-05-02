package com.so.luotk.models.output;

import java.util.ArrayList;

import lombok.Data;

@Data
public class GetAttendenceResponse {
    private String success;
    private String status;
    private ArrayList<GetAttendenceResult> result;
    private Extra extra;

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

    public ArrayList<GetAttendenceResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<GetAttendenceResult> result) {
        this.result = result;
    }

    public Extra getExtra() {
        return extra;
    }

    public void setExtra(Extra extra) {
        this.extra = extra;
    }
}


