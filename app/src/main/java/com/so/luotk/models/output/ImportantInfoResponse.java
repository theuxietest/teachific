package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ImportantInfoResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("result")
    @Expose
    private OrgInfoResult result = null;
    @SerializedName("extra")
    @Expose
    private ImportantInfoExtra extra;

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

    public OrgInfoResult getResult() {
        return result;
    }

    public void setResult(OrgInfoResult result) {
        this.result = result;
    }

    public ImportantInfoExtra getExtra() {
        return extra;
    }

    public void setExtra(ImportantInfoExtra extra) {
        this.extra = extra;
    }
}
