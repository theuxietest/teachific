
package com.so.luotk.models.newmodels.adminBatchModel;

import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Data;

@Data
public class Result extends BaseObservable implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("batchName")
    @Expose
    private String batchName;
    @SerializedName("batchCode")
    @Expose
    private String batchCode;
    @SerializedName("batchFee")
    @Expose
    private String batchFee;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private Object endDate;
    @SerializedName("fk_catId")
    @Expose
    private Integer fkCatId;
    @SerializedName("fk_courseId")
    @Expose
    private Integer fkCourseId;
    @SerializedName("fk_subjectId")
    @Expose
    private Integer fkSubjectId;
    @SerializedName("days_time")
    @Expose
    private String daysTime;
    @SerializedName("fk_orgId")
    @Expose
    private Integer fkOrgId;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("hexColor")
    @Expose
    private String hexColor;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("courseName")
    @Expose
    private String courseName;
    @SerializedName("subject")
    @Expose
    private Subject subject;
    @SerializedName("course")
    @Expose
    private Course course;
    @SerializedName("select")
    @Expose
    private boolean selected;


    @BindingAdapter("getDays")
    public static void convertStringToJson(TextView view, String response) {
        List<String> dayList = new ArrayList<>();
        String dayNames;
        //   String response = "{\"MON\":{\"day\":\"Monday\",\"startTime\":\"03:36\",\"endTime\":\"06:36\"},\"TUE\":{\"day\":\"Tuesday\",\"startTime\":\"01:43 PM\",\"endTime\":\"05:43 PM\"},\"WED\":{\"day\":\"Wednesday\",\"startTime\":\"02:19 PM\",\"endTime\":\"04:43 PM\"}}";
        try {
            JSONObject jsonObject = (JSONObject) new JSONObject(response);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONObject value = (JSONObject) jsonObject.get(key);
                    String day = (String) value.get("day");
                    dayList.add(day);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            StringBuilder sbString = new StringBuilder();
            for (String days : dayList) {
                sbString.append(days.charAt(0)).append(",");
            }
            dayNames = sbString.toString();
            if (dayNames.length() > 0)
                dayNames = dayNames.substring(0, dayNames.length() - 1);
            view.setText(dayNames);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getBatchFee() {
        return batchFee;
    }

    public void setBatchFee(String batchFee) {
        this.batchFee = batchFee;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Object getEndDate() {
        return endDate;
    }

    public void setEndDate(Object endDate) {
        this.endDate = endDate;
    }

    public Integer getFkCatId() {
        return fkCatId;
    }

    public void setFkCatId(Integer fkCatId) {
        this.fkCatId = fkCatId;
    }

    public Integer getFkCourseId() {
        return fkCourseId;
    }

    public void setFkCourseId(Integer fkCourseId) {
        this.fkCourseId = fkCourseId;
    }

    public Integer getFkSubjectId() {
        return fkSubjectId;
    }

    public void setFkSubjectId(Integer fkSubjectId) {
        this.fkSubjectId = fkSubjectId;
    }

    public String getDaysTime() {
        return daysTime;
    }

    public void setDaysTime(String daysTime) {
        this.daysTime = daysTime;
    }

    public Integer getFkOrgId() {
        return fkOrgId;
    }

    public void setFkOrgId(Integer fkOrgId) {
        this.fkOrgId = fkOrgId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
