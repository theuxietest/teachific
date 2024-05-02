package com.so.luotk.models.output;

import java.io.Serializable;

import lombok.Data;

@Data
public class BatchListResult implements Serializable {
    private String batchName;

    private String end_date;

    private String fk_subjectId;

    private String color;

    private String fk_orgId;

    private Subject subject;

    private String batchCode;

    private String created_at;

    private String deleted_at;

    private String fk_courseId;

    private String courseName;

    private String updated_at;

    private String fk_catId;

    private String days_time;

    //  private Pivot pivot;

    private String hexColor;

    private Course course;

    private String id;

    private String start_date;

    private String subjectName;

    //private Settings settings;

    private GetBatchSettingsResult settings;

    private boolean isBatchNotify = false;

    private boolean isAssignmentNotify = false;

    private boolean isTestNotify = false;

    private boolean isAnnouncementNotify = false;

    private boolean isVideoNotify = false;

    private boolean isLiveStartNotify = false;

    private String notificationType;

    private boolean isNewBatch;

    private boolean selected;

    private int students_num;

    private String batchFee;

    public String getBatchFee() {
        return batchFee;
    }

    public void setBatchFee(String batchFee) {
        this.batchFee = batchFee;
    }

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

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
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

    public String getFk_courseId() {
        return fk_courseId;
    }

    public void setFk_courseId(String fk_courseId) {
        this.fk_courseId = fk_courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public GetBatchSettingsResult getSettings() {
        return settings;
    }

    public void setSettings(GetBatchSettingsResult settings) {
        this.settings = settings;
    }

    public boolean isBatchNotify() {
        return isBatchNotify;
    }

    public void setBatchNotify(boolean batchNotify) {
        isBatchNotify = batchNotify;
    }

    public boolean isAssignmentNotify() {
        return isAssignmentNotify;
    }

    public void setAssignmentNotify(boolean assignmentNotify) {
        isAssignmentNotify = assignmentNotify;
    }

    public boolean isTestNotify() {
        return isTestNotify;
    }

    public void setTestNotify(boolean testNotify) {
        isTestNotify = testNotify;
    }

    public boolean isAnnouncementNotify() {
        return isAnnouncementNotify;
    }

    public void setAnnouncementNotify(boolean announcementNotify) {
        isAnnouncementNotify = announcementNotify;
    }

    public boolean isVideoNotify() {
        return isVideoNotify;
    }

    public void setVideoNotify(boolean videoNotify) {
        isVideoNotify = videoNotify;
    }

    public boolean isLiveStartNotify() {
        return isLiveStartNotify;
    }

    public void setLiveStartNotify(boolean liveStartNotify) {
        isLiveStartNotify = liveStartNotify;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isNewBatch() {
        return isNewBatch;
    }

    public void setNewBatch(boolean newBatch) {
        isNewBatch = newBatch;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getStudents_num() {
        return students_num;
    }

    public void setStudents_num(int students_num) {
        this.students_num = students_num;
    }
}
