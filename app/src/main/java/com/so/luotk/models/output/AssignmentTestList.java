package com.so.luotk.models.output;

import lombok.Data;

@Data
public class AssignmentTestList {
    private String topicName;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
