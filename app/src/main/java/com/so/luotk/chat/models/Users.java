package com.so.luotk.chat.models;

public class Users {
    String profilePic, userName, mail, userMobile, userId, lastMessage, status;

    public Users(String profilePic, String userName, String mail, String userMobile, String userId, String lastMessage, String status) {
        this.profilePic = profilePic;
        this.userName = userName;
        this.mail = mail;
        this.userMobile = userMobile;
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.status = status;
    }

    public Users(String userName, String userMobile, String userId) {
        this.userName = userName;
        this.userMobile = userMobile;
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
