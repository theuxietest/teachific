package com.so.luotk.models.newmodels.adminBatchModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UpdateAssignmentModel {
    @SerializedName("students")
    @Expose
    public ArrayList<String> students;

    public ArrayList<String> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<String> students) {
        this.students = students;
    }


    /*  @SerializedName("old_attachments[]")
    @Expose
    public String old_attachments;*/
}
