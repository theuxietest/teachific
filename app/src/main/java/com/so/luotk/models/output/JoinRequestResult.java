package com.so.luotk.models.output;

import androidx.databinding.BaseObservable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class JoinRequestResult extends BaseObservable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("fk_userId")
    @Expose
    private Integer fkUserId;
    @SerializedName("batchCode")
    @Expose
    private String batchCode;
    @SerializedName("fk_batchId")
    @Expose
    private Integer fkBatchId;
    @SerializedName("fk_orgId")
    @Expose
    private Integer fkOrgId;
    @SerializedName("status")
    @Expose
    private Object status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Integer fkUserId) {
        this.fkUserId = fkUserId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Integer getFkBatchId() {
        return fkBatchId;
    }

    public void setFkBatchId(Integer fkBatchId) {
        this.fkBatchId = fkBatchId;
    }

    public Integer getFkOrgId() {
        return fkOrgId;
    }

    public void setFkOrgId(Integer fkOrgId) {
        this.fkOrgId = fkOrgId;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
