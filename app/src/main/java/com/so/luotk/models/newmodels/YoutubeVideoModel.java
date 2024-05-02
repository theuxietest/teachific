package com.so.luotk.models.newmodels;

public class YoutubeVideoModel {
    private String videoCode;
    private String description;
    private String videoUrl;

    public YoutubeVideoModel() {
    }

    public YoutubeVideoModel(String videoCode, String description, String videoUrl) {
        this.videoCode = videoCode;
        this.description = description;
        this.videoUrl = videoUrl;
    }

    public String getVideoCode() {
        return videoCode;
    }

    public void setVideoCode(String videoCode) {
        this.videoCode = videoCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
