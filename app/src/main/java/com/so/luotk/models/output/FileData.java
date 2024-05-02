package com.so.luotk.models.output;

import java.io.Serializable;

import lombok.Data;

@Data
public class FileData implements Serializable {
    private String videoUrl;

    private String updated_at;

    private String thumb;

    private String created_at;

    private String id;

    private String fk_batchId;

    private String fk_courseId;

    private String title;

    private String fk_folderId;

    private String deleted_at;

    private int is_locked = -1;

    private String fk_orgId;

    private boolean isPlaying;

    private int views_limit = -1;

    private int user_viewed_count = -1;


    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
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

    public String getFk_courseId() {
        return fk_courseId;
    }

    public void setFk_courseId(String fk_courseId) {
        this.fk_courseId = fk_courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFk_folderId() {
        return fk_folderId;
    }

    public void setFk_folderId(String fk_folderId) {
        this.fk_folderId = fk_folderId;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public int getIs_locked() {
        return is_locked;
    }

    public void setIs_locked(int is_locked) {
        this.is_locked = is_locked;
    }

    public String getFk_orgId() {
        return fk_orgId;
    }

    public void setFk_orgId(String fk_orgId) {
        this.fk_orgId = fk_orgId;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getViews_limit() {
        return views_limit;
    }

    public void setViews_limit(int views_limit) {
        this.views_limit = views_limit;
    }

    public int getUser_viewed_count() {
        return user_viewed_count;
    }

    public void setUser_viewed_count(int user_viewed_count) {
        this.user_viewed_count = user_viewed_count;
    }
}
