package com.so.luotk.models.output;

import java.io.Serializable;

import lombok.Data;

@Data
public class GetBatchSettingsResult implements Serializable {
    private String manageAssignment;

    private String manageVideo;

    private String test;

    private String study_material;

    private String attendance;

    private String manageStudent;

    private String live;

    private String announcement;

    public GetBatchSettingsResult() {

    }

    public GetBatchSettingsResult(String manageVideo, String test, String study_material, String live) {

        this.manageVideo = manageVideo;
        this.test = test;
        this.study_material = study_material;
        this.live = live;

    }

    public String getManageAssignment() {
        return manageAssignment;
    }

    public void setManageAssignment(String manageAssignment) {
        this.manageAssignment = manageAssignment;
    }

    public String getManageVideo() {
        return manageVideo;
    }

    public void setManageVideo(String manageVideo) {
        this.manageVideo = manageVideo;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getStudy_material() {
        return study_material;
    }

    public void setStudy_material(String study_material) {
        this.study_material = study_material;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getManageStudent() {
        return manageStudent;
    }

    public void setManageStudent(String manageStudent) {
        this.manageStudent = manageStudent;
    }

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }
}
