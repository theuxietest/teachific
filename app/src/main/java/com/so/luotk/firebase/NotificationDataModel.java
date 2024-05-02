package com.so.luotk.firebase;

import java.io.Serializable;

import lombok.Data;

@Data
public class NotificationDataModel implements Serializable {
    private String batchId = "-1";
    private String notificationType = " ";
    private String id = "-1";
    private String courseId = "-1";

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
