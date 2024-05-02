package com.so.luotk.models.output;

import java.io.Serializable;

@lombok.Data
public class Data implements Serializable {

    private String notes;

    private int test_type;

    private String submitDate;

    private int fk_bulkTestId;

    private int show_test_result;

    private int when_to_display_result;

    private String test_result_display_date;

    private String test_result_display_time;

    private String user_answer;

    private String submit_date;

    private String startDate;

    private String totalMarks;

    private String totalQuestion;

    private String startTime;

    private String created_at;

    private String deleted_at;

    private String fk_createdBy;

    private String isSms;

    private String isReminder;

    private String submitTime;

    private String attachment;

    private String answer;

    private String updated_at;

    private String topic;

    private String id;

    private String fk_batchId;

    private int status;

    private String duration;

    private BatchData batch;

    private boolean isNewItem;

    private int is_locked = -1;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getTest_type() {
        return test_type;
    }

    public void setTest_type(int test_type) {
        this.test_type = test_type;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public int getFk_bulkTestId() {
        return fk_bulkTestId;
    }

    public void setFk_bulkTestId(int fk_bulkTestId) {
        this.fk_bulkTestId = fk_bulkTestId;
    }

    public int getShow_test_result() {
        return show_test_result;
    }

    public void setShow_test_result(int show_test_result) {
        this.show_test_result = show_test_result;
    }

    public int getWhen_to_display_result() {
        return when_to_display_result;
    }

    public void setWhen_to_display_result(int when_to_display_result) {
        this.when_to_display_result = when_to_display_result;
    }

    public String getTest_result_display_date() {
        return test_result_display_date;
    }

    public void setTest_result_display_date(String test_result_display_date) {
        this.test_result_display_date = test_result_display_date;
    }

    public String getTest_result_display_time() {
        return test_result_display_time;
    }

    public void setTest_result_display_time(String test_result_display_time) {
        this.test_result_display_time = test_result_display_time;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(String totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(String totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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

    public String getFk_createdBy() {
        return fk_createdBy;
    }

    public void setFk_createdBy(String fk_createdBy) {
        this.fk_createdBy = fk_createdBy;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFk_batchId() {
        return fk_batchId;
    }

    public void setFk_batchId(String fk_batchId) {
        this.fk_batchId = fk_batchId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public BatchData getBatch() {
        return batch;
    }

    public void setBatch(BatchData batch) {
        this.batch = batch;
    }

    public boolean isNewItem() {
        return isNewItem;
    }

    public void setNewItem(boolean newItem) {
        isNewItem = newItem;
    }

    public int getIs_locked() {
        return is_locked;
    }

    public void setIs_locked(int is_locked) {
        this.is_locked = is_locked;
    }
}
