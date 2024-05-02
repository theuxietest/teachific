package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
@Data
public class GetCourseSettingsResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("result")
    @Expose
    private GetBatchSettingsResult result;
    @SerializedName("extra")
    @Expose
    private CourseSettingExtra extra;

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

    public GetBatchSettingsResult getResult() {
        return result;
    }

    public void setResult(GetBatchSettingsResult result) {
        this.result = result;
    }

    public CourseSettingExtra getExtra() {
        return extra;
    }

    public void setExtra(CourseSettingExtra extra) {
        this.extra = extra;
    }
}
