package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class OtherCourses {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("fk_orgId")
    @Expose
    private Integer fkOrgId;
    @SerializedName("course_name")
    @Expose
    private String courseName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("sub_category")
    @Expose
    private String subCategory;
    @SerializedName("duration_type")
    @Expose
    private Integer durationType;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("duration_in")
    @Expose
    private int durationIn;
    @SerializedName("expiry_date")
    @Expose
    private String expiryDate;
    @SerializedName("actual_price")
    @Expose
    private String actualPrice;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("selling_price")
    @Expose
    private String sellingPrice;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("terms_and_condition")
    @Expose
    private Integer termsAndCondition;
    @SerializedName("restrict_video")
    @Expose
    private Integer restrictVideo;
    @SerializedName("likes_count")
    @Expose
    private Integer likesCount;
    @SerializedName("is_published")
    @Expose
    private Integer isPublished;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("is_liked")
    @Expose
    private Integer isLiked;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFkOrgId() {
        return fkOrgId;
    }

    public void setFkOrgId(Integer fkOrgId) {
        this.fkOrgId = fkOrgId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Integer getDurationType() {
        return durationType;
    }

    public void setDurationType(Integer durationType) {
        this.durationType = durationType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getDurationIn() {
        return durationIn;
    }

    public void setDurationIn(int durationIn) {
        this.durationIn = durationIn;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(String actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getTermsAndCondition() {
        return termsAndCondition;
    }

    public void setTermsAndCondition(Integer termsAndCondition) {
        this.termsAndCondition = termsAndCondition;
    }

    public Integer getRestrictVideo() {
        return restrictVideo;
    }

    public void setRestrictVideo(Integer restrictVideo) {
        this.restrictVideo = restrictVideo;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Integer isPublished) {
        this.isPublished = isPublished;
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

    public Integer getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Integer isLiked) {
        this.isLiked = isLiked;
    }
}

