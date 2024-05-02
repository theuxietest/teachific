package com.so.luotk.models.output;


import lombok.Data;

@Data
public class CourseSettingExtra {
    private String courseId;
    private String courseName;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
