package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class ReportCountResult {
    @SerializedName("batchTestCount")
    @Expose
    private Integer batchTestCount;
    @SerializedName("courseTestCount")
    @Expose
    private Integer courseTestCount;
    @SerializedName("batchAssignmentCount")
    @Expose
    private Integer batchAssignmentCount;

    public Integer getBatchTestCount() {
        return batchTestCount;
    }

    public void setBatchTestCount(Integer batchTestCount) {
        this.batchTestCount = batchTestCount;
    }

    public Integer getCourseTestCount() {
        return courseTestCount;
    }

    public void setCourseTestCount(Integer courseTestCount) {
        this.courseTestCount = courseTestCount;
    }

    public Integer getBatchAssignmentCount() {
        return batchAssignmentCount;
    }

    public void setBatchAssignmentCount(Integer batchAssignmentCount) {
        this.batchAssignmentCount = batchAssignmentCount;
    }
}
