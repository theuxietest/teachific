package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;


@Data
public class CourseListResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("result")
    @Expose
    private CourseResult result;
    @SerializedName("extra")
    @Expose
    private CourseExtra extra;

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

    public CourseResult getResult() {
        return result;
    }

    public void setResult(CourseResult result) {
        this.result = result;
    }

    public CourseExtra getExtra() {
        return extra;
    }

    public void setExtra(CourseExtra extra) {
        this.extra = extra;
    }
}
