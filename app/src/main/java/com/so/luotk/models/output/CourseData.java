package com.so.luotk.models.output;

import java.io.Serializable;

import lombok.Data;

@Data
public class CourseData implements Serializable {

    private String id;
    private String course_name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }
}
