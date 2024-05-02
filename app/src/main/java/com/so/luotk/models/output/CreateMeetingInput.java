package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CreateMeetingInput {
    public CreateMeetingInput() {
        this.type = 1;
        this.timezone = "Asia/Calcutta";
        this.topic = "Smartowls Meeting";
    }

    public CreateMeetingInput(String topicName) {
        this.type = 1;
        this.timezone = "Asia/Calcutta";
        this.topic = topicName;
    }


    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("topic")
    @Expose
    private String topic;

    public Integer getType() {
        return 1;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTimezone() {
        return "Asia/Calcutta";
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
