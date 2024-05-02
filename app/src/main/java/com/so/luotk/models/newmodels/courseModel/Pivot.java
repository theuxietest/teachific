package com.so.luotk.models.newmodels.courseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class Pivot implements Serializable {
    @SerializedName("fk_userId")
    @Expose
    private Integer fkUserId;
    @SerializedName("fk_courseId")
    @Expose
    private Integer fkCourseId;
    @SerializedName("enroll_date")
    @Expose
    private String enrollDate;
    @SerializedName("expiry_date")
    @Expose
    private Object expiryDate;
    @SerializedName("amount_paid")
    @Expose
    private String amountPaid;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;

    public Integer getFkUserId() {
        return fkUserId;
    }

    public void setFkUserId(Integer fkUserId) {
        this.fkUserId = fkUserId;
    }

    public Integer getFkCourseId() {
        return fkCourseId;
    }

    public void setFkCourseId(Integer fkCourseId) {
        this.fkCourseId = fkCourseId;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public Object getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Object expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
