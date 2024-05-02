package com.so.luotk.models.video;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DatumVideo implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("fk_batchId")
    @Expose
    private String fkBatchId;
    @SerializedName("folderName")
    @Expose
    private String folderName;
    @SerializedName("folderId")
    @Expose
    private String folderId;
    @SerializedName("fk_courseId")
    @Expose
    private String fk_courseId;
    @SerializedName("videoUrl")
    @Expose
    private String videoUrl;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("thumb")
    @Expose
    private String thumb;
    @SerializedName("views_limit")
    @Expose
    private int views_limit;
    @SerializedName("is_locked")
    @Expose
    private Integer is_locked=-1;

    private int user_viewed_count = -1;

    private boolean isPlaying;

    private boolean isNewItem;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNewItem() {
        return isNewItem;
    }

    public void setNewItem(boolean newItem) {
        isNewItem = newItem;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getUser_viewed_count() {
        return user_viewed_count;
    }

    public void setUser_viewed_count(int user_viewed_count) {
        this.user_viewed_count = user_viewed_count;
    }

    public int getViews_limit() {
        return views_limit;
    }

    public void setViews_limit(int views_limit) {
        this.views_limit = views_limit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFkBatchId() {
        return fkBatchId;
    }

    public void setFkBatchId(String fkBatchId) {
        this.fkBatchId = fkBatchId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFk_courseId() {
        return fk_courseId;
    }

    public void setFk_courseId(String fk_courseId) {
        this.fk_courseId = fk_courseId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }


    public Integer getIs_locked() {
        return is_locked;
    }

    public void setIs_locked(Integer is_locked) {
        this.is_locked = is_locked;
    }
}
