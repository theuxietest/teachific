package com.so.luotk.models.output;

import lombok.Data;

@Data
public class Extra {
    private String batch_start_date;

    //for overview
    private String courseName;

    private String batchCode;

    private String subjectName;

    private String hexColor;

    private String  batchId;

    private String batchName;

    public String getBatch_start_date() {
        return batch_start_date;
    }

    public void setBatch_start_date(String batch_start_date) {
        this.batch_start_date = batch_start_date;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }
}
