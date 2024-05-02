package com.so.luotk.models.output;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class CourseResult extends BaseObservable {
    @SerializedName("purchasedCourses")
    @Expose
    private List<OtherCourses> purchasedCourses;
    @SerializedName("otherCourses")
    @Expose
    private List<OtherCourses> otherCourses;

    @BindingAdapter("isVisible")
    public static void hideView(View view, boolean flag) {
        if (flag) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public List<OtherCourses> getPurchasedCourses() {
        return purchasedCourses;
    }

    public void setPurchasedCourses(List<OtherCourses> purchasedCourses) {
        this.purchasedCourses = purchasedCourses;
    }

    public List<OtherCourses> getOtherCourses() {
        return otherCourses;
    }

    public void setOtherCourses(List<OtherCourses> otherCourses) {
        this.otherCourses = otherCourses;
    }
}
