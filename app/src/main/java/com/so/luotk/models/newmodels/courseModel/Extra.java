
package com.so.luotk.models.newmodels.courseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Extra {

    @SerializedName("duration_type")
    @Expose
    private DurationIn durationType;
    @SerializedName("duration_in")
    @Expose
    private DurationIn durationIn;
    @SerializedName("image_base_url")
    @Expose
    private String imageBaseUrl;

    public DurationIn getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationIn durationType) {
        this.durationType = durationType;
    }

    public DurationIn getDurationIn() {
        return durationIn;
    }

    public void setDurationIn(DurationIn durationIn) {
        this.durationIn = durationIn;
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public void setImageBaseUrl(String imageBaseUrl) {
        this.imageBaseUrl = imageBaseUrl;
    }
}
