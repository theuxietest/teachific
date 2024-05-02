package com.so.luotk.models.output;

import lombok.Data;

@Data
public class GetAttendenceResult {
    private String lecture_name;

    private String batch_start_date;

    private String lecture_date;

    private String status;

    private String lecture_timing;

    private String userId;

    private String student_name;

    public String getLecture_name() {
        return lecture_name;
    }

    public void setLecture_name(String lecture_name) {
        this.lecture_name = lecture_name;
    }

    public String getBatch_start_date() {
        return batch_start_date;
    }

    public void setBatch_start_date(String batch_start_date) {
        this.batch_start_date = batch_start_date;
    }

    public String getLecture_date() {
        return lecture_date;
    }

    public void setLecture_date(String lecture_date) {
        this.lecture_date = lecture_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLecture_timing() {
        return lecture_timing;
    }

    public void setLecture_timing(String lecture_timing) {
        this.lecture_timing = lecture_timing;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }
}
