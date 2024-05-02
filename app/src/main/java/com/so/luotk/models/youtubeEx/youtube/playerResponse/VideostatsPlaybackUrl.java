package com.so.luotk.models.youtubeEx.youtube.playerResponse;

import java.io.Serializable;

public class VideostatsPlaybackUrl implements Serializable {
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String toString() {
        return
                "VideostatsPlaybackUrl{" +
                        "baseUrl = '" + baseUrl + '\'' +
                        "}";
    }
}
