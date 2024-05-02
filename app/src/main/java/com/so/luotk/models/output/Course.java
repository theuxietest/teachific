package com.so.luotk.models.output;

import lombok.Data;

@Data
public class Course {
    private String image;

    private String courseName;

    private String updated_at;

    private String fk_catId;

    private String created_at;

    private String id;

    private String deleted_at;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getFk_catId() {
        return fk_catId;
    }

    public void setFk_catId(String fk_catId) {
        this.fk_catId = fk_catId;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }
}
