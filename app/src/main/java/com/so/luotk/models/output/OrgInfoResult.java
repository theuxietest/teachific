package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.so.luotk.models.video.DatumVideo;

import java.util.List;

import lombok.Data;

@Data
public class OrgInfoResult {
    @SerializedName("testimonial")
    @Expose
    private List<DatumVideo> testimonial = null;
    @SerializedName("popular")
    @Expose
    private List<DatumVideo> popular = null;
    @SerializedName("files")
    @Expose
    private List<ImportantInfoResult> files = null;

    public List<DatumVideo> getTestimonial() {
        return testimonial;
    }

    public void setTestimonial(List<DatumVideo> testimonial) {
        this.testimonial = testimonial;
    }

    public List<DatumVideo> getPopular() {
        return popular;
    }

    public void setPopular(List<DatumVideo> popular) {
        this.popular = popular;
    }

    public List<ImportantInfoResult> getFiles() {
        return files;
    }

    public void setFiles(List<ImportantInfoResult> files) {
        this.files = files;
    }
}
