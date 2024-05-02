package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CourseExtra {
    @SerializedName("duration_type")
    @Expose
    private DurationType durationType;
    @SerializedName("duration_in")
    @Expose
    private DurationType durationIn;
    @SerializedName("image_base_url")
    @Expose
    private String imageBaseUrl;

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
    }

    public DurationType getDurationIn() {
        return durationIn;
    }

    public void setDurationIn(DurationType durationIn) {
        this.durationIn = durationIn;
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public void setImageBaseUrl(String imageBaseUrl) {
        this.imageBaseUrl = imageBaseUrl;
    }
}
