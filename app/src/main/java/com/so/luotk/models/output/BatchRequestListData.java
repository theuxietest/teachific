package com.so.luotk.models.output;

import lombok.Data;

@Data
public class BatchRequestListData {
    private String batchName;

    private String end_date;

    private String fk_subjectId;

    private String color;

    private String fk_orgId;

    private String batchCode;

    private String batchFee;

    private String created_at;

    private String deleted_at;

    private String hexColor;

    private String fk_courseId;

    private String updated_at;

    private String fk_catId;

    private String days_time;

    private String id;

    private String start_date;

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getFk_subjectId() {
        return fk_subjectId;
    }

    public void setFk_subjectId(String fk_subjectId) {
        this.fk_subjectId = fk_subjectId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFk_orgId() {
        return fk_orgId;
    }

    public void setFk_orgId(String fk_orgId) {
        this.fk_orgId = fk_orgId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getBatchFee() {
        return batchFee;
    }

    public void setBatchFee(String batchFee) {
        this.batchFee = batchFee;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public String getFk_courseId() {
        return fk_courseId;
    }

    public void setFk_courseId(String fk_courseId) {
        this.fk_courseId = fk_courseId;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getFk_catId() {
        return fk_catId;
    }

    public void setFk_catId(String fk_catId) {
        this.fk_catId = fk_catId;
    }

    public String getDays_time() {
        return days_time;
    }

    public void setDays_time(String days_time) {
        this.days_time = days_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }
}
