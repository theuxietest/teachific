
package com.so.luotk.models.newmodels.courseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("purchasedCourses")
    @Expose
    private PurchasedCourses purchasedCourses;
    @SerializedName("otherCourses")
    @Expose
    private OtherCourses otherCourses;

    public PurchasedCourses getPurchasedCourses() {
        return purchasedCourses;
    }

    public void setPurchasedCourses(PurchasedCourses purchasedCourses) {
        this.purchasedCourses = purchasedCourses;
    }

    public OtherCourses getOtherCourses() {
        return otherCourses;
    }

    public void setOtherCourses(OtherCourses otherCourses) {
        this.otherCourses = otherCourses;
    }

}
