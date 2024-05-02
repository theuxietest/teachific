package com.so.luotk.chat.models;

public class ChatUser {
    String AdminId;
    String username;
    String status;
    String phone;
    String email;

    public ChatUser(String AdminId, String username, String status, String phone, String email) {
        this.AdminId = AdminId;
        this.username = username;
        this.status = status;
        this.phone = phone;
        this.email = email;
    }

    public ChatUser() {
    }

    public String getAdminId() {
        return AdminId;
    }

    public void setAdminId(String id) {
        this.AdminId = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
