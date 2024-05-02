
package com.so.luotk.models.newmodels.adminBatchModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Course {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("image")
    @Expose
    private Object image;
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
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
