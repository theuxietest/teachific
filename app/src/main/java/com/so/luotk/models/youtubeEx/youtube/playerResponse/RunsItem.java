package com.so.luotk.models.youtubeEx.youtube.playerResponse;

import java.io.Serializable;

public class RunsItem implements Serializable {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return
                "RunsItem{" +
                        "text = '" + text + '\'' +
                        "}";
    }
}
