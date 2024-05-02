package com.so.luotk.models.output;

import lombok.Data;

@Data
public class TestList {
    private String topicName;
    private String duration;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
