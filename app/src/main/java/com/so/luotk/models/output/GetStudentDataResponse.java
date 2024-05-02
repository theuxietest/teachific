package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GetStudentDataResponse {
//    private String name;

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("result")
    @Expose
    public StudentDetails result;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public StudentDetails getResult() {
        return result;
    }

    public void setResult(StudentDetails result) {
        this.result = result;
    }
}
