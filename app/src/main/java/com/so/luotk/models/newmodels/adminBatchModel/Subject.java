
package com.so.luotk.models.newmodels.adminBatchModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subject {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("image")
    @Expose
    private Object image;
    @SerializedName("fk_courseId")
    @Expose
    private Integer fkCourseId;
    @SerializedName("fk_catId")
    @Expose
    private Integer fkCatId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

    public Integer getFkCourseId() {
        return fkCourseId;
    }

    public void setFkCourseId(Integer fkCourseId) {
        this.fkCourseId = fkCourseId;
    }

    public Integer getFkCatId() {
        return fkCatId;
    }

    public void setFkCatId(Integer fkCatId) {
        this.fkCatId = fkCatId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

}
