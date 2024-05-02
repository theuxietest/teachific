package com.so.luotk.models.input;

import lombok.Data;

@Data
public class GetAttendenceInput {
    private String batchId;
    private String year_month;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getYear_month() {
        return year_month;
    }

    public void setYear_month(String year_month) {
        this.year_month = year_month;
    }
}
