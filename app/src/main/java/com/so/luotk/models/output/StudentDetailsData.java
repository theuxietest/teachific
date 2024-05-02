package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class StudentDetailsData implements Serializable {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("email")
    @Expose
    public Object email;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("email_verified_at")
    @Expose
    public Object emailVerifiedAt;
    @SerializedName("orgId")
    @Expose
    public Integer orgId;
    @SerializedName("orgCode")
    @Expose
    public String orgCode;
    @SerializedName("otp")
    @Expose
    public Integer otp;
    @SerializedName("parent1Name")
    @Expose
    public Object parent1Name;
    @SerializedName("parent1Mobile")
    @Expose
    public Object parent1Mobile;
    @SerializedName("parent1Email")
    @Expose
    public Object parent1Email;
    @SerializedName("parent2Name")
    @Expose
    public Object parent2Name;
    @SerializedName("parent2Mobile")
    @Expose
    public Object parent2Mobile;
    @SerializedName("parent2Email")
    @Expose
    public Object parent2Email;
    @SerializedName("isActive")
    @Expose
    public Integer isActive;
    @SerializedName("photo")
    @Expose
    public Object photo;
    @SerializedName("dob")
    @Expose
    public Object dob;
    @SerializedName("bloodGroup")
    @Expose
    public Object bloodGroup;
    @SerializedName("gender")
    @Expose
    public Object gender;
    @SerializedName("college")
    @Expose
    public Object college;
    @SerializedName("school")
    @Expose
    public Object school;
    @SerializedName("device_id")
    @Expose
    public String deviceId;
    @SerializedName("fcm_token")
    @Expose
    public String fcmToken;
    @SerializedName("device_type")
    @Expose
    public String deviceType;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    public Object deletedAt;

    private boolean selected;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Object getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(Object emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public Object getParent1Name() {
        return parent1Name;
    }

    public void setParent1Name(Object parent1Name) {
        this.parent1Name = parent1Name;
    }

    public Object getParent1Mobile() {
        return parent1Mobile;
    }

    public void setParent1Mobile(Object parent1Mobile) {
        this.parent1Mobile = parent1Mobile;
    }

    public Object getParent1Email() {
        return parent1Email;
    }

    public void setParent1Email(Object parent1Email) {
        this.parent1Email = parent1Email;
    }

    public Object getParent2Name() {
        return parent2Name;
    }

    public void setParent2Name(Object parent2Name) {
        this.parent2Name = parent2Name;
    }

    public Object getParent2Mobile() {
        return parent2Mobile;
    }

    public void setParent2Mobile(Object parent2Mobile) {
        this.parent2Mobile = parent2Mobile;
    }

    public Object getParent2Email() {
        return parent2Email;
    }

    public void setParent2Email(Object parent2Email) {
        this.parent2Email = parent2Email;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Object getPhoto() {
        return photo;
    }

    public void setPhoto(Object photo) {
        this.photo = photo;
    }

    public Object getDob() {
        return dob;
    }

    public void setDob(Object dob) {
        this.dob = dob;
    }

    public Object getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(Object bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Object getGender() {
        return gender;
    }

    public void setGender(Object gender) {
        this.gender = gender;
    }

    public Object getCollege() {
        return college;
    }

    public void setCollege(Object college) {
        this.college = college;
    }

    public Object getSchool() {
        return school;
    }

    public void setSchool(Object school) {
        this.school = school;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
