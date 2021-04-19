package com.agamilabs.smartshop.FireInboxShow;

public class BatiChatMsgModel {
    String chatId, message, sentBy ;
    Object sentTime ;

    public BatiChatMsgModel(String chatId, String message, String sentBy, Object sentTime) {
        this.chatId = chatId;
        this.message = message;
        this.sentBy = sentBy;
        this.sentTime = sentTime;
    }

    public BatiChatMsgModel() {
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public Object getSentTime() {
        return sentTime;
    }

    public void setSentTime(Object sentTime) {
        this.sentTime = sentTime;
    }

    @Override
    public String toString() {
        return "BatiChatMsgModel{" +
                "chatId='" + chatId + '\'' +
                ", message='" + message + '\'' +
                ", sentBy='" + sentBy + '\'' +
                ", sentTime=" + sentTime +
                '}';
    }
}
