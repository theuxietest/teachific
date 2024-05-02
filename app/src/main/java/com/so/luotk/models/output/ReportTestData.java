package com.so.luotk.models.output;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReportTestData implements Serializable {
    private String notes;

    private String correct;

    private String user_answer;

    private String submit_date;

    private String submitDate;

    private String created_at;

    private String totalMarks;

    private String deleted_at;

    private String totalMarksObtained;

    private String fk_createdBy;

    private String wrong;

    private String duration;

    private String isSms;

    private String isReminder;

    private String submitTime;

    private String attachment;

    private String answer;

    private String updated_at;

    private String topic;

    // private Pivot pivot;
    private int test_type;

    private String id;

    private String attempted;

    private String fk_batchId;

    private String totalQuestion;

    private BatchData batch;

    private CourseData course;

    private boolean isNewItem;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getUser_answer() {
        return user_answer;
    }

    public void setUser_answer(String user_answer) {
        this.user_answer = user_answer;
    }

    public String getSubmit_date() {
        return submit_date;
    }

    public void setSubmit_date(String submit_date) {
        this.submit_date = submit_date;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(String totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getTotalMarksObtained() {
        return totalMarksObtained;
    }

    public void setTotalMarksObtained(String totalMarksObtained) {
        this.totalMarksObtained = totalMarksObtained;
    }

    public String getFk_createdBy() {
        return fk_createdBy;
    }

    public void setFk_createdBy(String fk_createdBy) {
        this.fk_createdBy = fk_createdBy;
    }

    public String getWrong() {
        return wrong;
    }

    public void setWrong(String wrong) {
        this.wrong = wrong;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getIsSms() {
        return isSms;
    }

    public void setIsSms(String isSms) {
        this.isSms = isSms;
    }

    public String getIsReminder() {
        return isReminder;
    }

    public void setIsReminder(String isReminder) {
        this.isReminder = isReminder;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getTest_type() {
        return test_type;
    }

    public void setTest_type(int test_type) {
        this.test_type = test_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttempted() {
        return attempted;
    }

    public void setAttempted(String attempted) {
        this.attempted = attempted;
    }

    public String getFk_batchId() {
        return fk_batchId;
    }

    public void setFk_batchId(String fk_batchId) {
        this.fk_batchId = fk_batchId;
    }

    public String getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(String totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public BatchData getBatch() {
        return batch;
    }

    public void setBatch(BatchData batch) {
        this.batch = batch;
    }

    public CourseData getCourse() {
        return course;
    }

    public void setCourse(CourseData course) {
        this.course = course;
    }

    public boolean isNewItem() {
        return isNewItem;
    }

    public void setNewItem(boolean newItem) {
        isNewItem = newItem;
    }
}
