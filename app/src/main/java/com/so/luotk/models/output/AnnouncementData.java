package com.so.luotk.models.output;

import lombok.Data;

@Data
public class AnnouncementData {
   // private String attachment;

    private String updated_at;

    private String created_at;

    private String id;

    private String fk_batchId;

    private String deleted_at;

    private String announcement;

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }
}
