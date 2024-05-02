package com.so.luotk.models.youtubeEx.youtube.playerResponse;

import java.io.Serializable;

public class AtrUrl implements Serializable {

    private String baseUrl;
    private int elapsedMediaTimeSeconds;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getElapsedMediaTimeSeconds() {
        return elapsedMediaTimeSeconds;
    }

    public void setElapsedMediaTimeSeconds(int elapsedMediaTimeSeconds) {
        this.elapsedMediaTimeSeconds = elapsedMediaTimeSeconds;
    }

    @Override
    public String toString() {
        return
                "AtrUrl{" +
                        "baseUrl = '" + baseUrl + '\'' +
                        ",elapsedMediaTimeSeconds = '" + elapsedMediaTimeSeconds + '\'' +
                        "}";
    }
}
