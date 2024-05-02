package com.so.luotk.models.output;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CreateMeetingResponse {
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("encrypted_password")
    @Expose
    private String encryptedPassword;
    @SerializedName("h323_password")
    @Expose
    private String h323Password;
    @SerializedName("host_email")
    @Expose
    private String hostEmail;
    @SerializedName("host_id")
    @Expose
    private String hostId;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("join_url")
    @Expose
    private String joinUrl;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("pstn_password")
    @Expose
    private String pstnPassword;
    @SerializedName("start_url")
    @Expose
    private String startUrl;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("uuid")
    @Expose
    private String uuid;


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getH323Password() {
        return h323Password;
    }

    public void setH323Password(String h323Password) {
        this.h323Password = h323Password;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public void setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPstnPassword() {
        return pstnPassword;
    }

    public void setPstnPassword(String pstnPassword) {
        this.pstnPassword = pstnPassword;
    }

    public String getStartUrl() {
        return startUrl;
    }

    public void setStartUrl(String startUrl) {
        this.startUrl = startUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
