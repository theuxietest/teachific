package com.so.luotk.models.output;

import com.google.gson.Gson;

public class MakeAnnouncementResponse {
    private boolean success;

    private int status;

    private String result;

    private Object message;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return new Gson().toJson(message);
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
