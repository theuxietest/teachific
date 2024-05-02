package com.so.luotk.chat.models;

public class MessageModel {
    String uId, messages, messageId, messageName;
    Long timestamp;
    private boolean isSelected = false;

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public MessageModel(String uId, String messages) {
        this.uId = uId;
        this.messages = messages;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public MessageModel(String uId, String messages, Long timestamp, String messageName) {
        this.uId = uId;
        this.messages = messages;
        this.timestamp = timestamp;
        this.messageName = messageName;
    }

    public MessageModel() {
    }

}