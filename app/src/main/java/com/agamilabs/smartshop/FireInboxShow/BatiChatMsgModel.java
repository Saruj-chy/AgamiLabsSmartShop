package com.agamilabs.smartshop.FireInboxShow;

public class BatiChatMsgModel {
    String documentId ;
    String chatId, message, sentBy ;
    Object sentTime ;

    public BatiChatMsgModel(String chatId, String message, String sentBy, Object sentTime) {
        this.chatId = chatId;
        this.message = message;
        this.sentBy = sentBy;
        this.sentTime = sentTime;
    }

    public BatiChatMsgModel(String documentId, String chatId, String message, String sentBy, Object sentTime) {
        this.documentId = documentId;
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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public String toString() {
        return "BatiChatMsgModel{" +
                "chatId='" + chatId + '\'' + "\n"+
                ", message='" + message + '\'' +"\n"+
                ", sentBy='" + sentBy + '\'' +"\n"+
                ", sentTime=" + sentTime +
                '}'+"\n\n";
    }
}
