package com.agamilabs.smartshop.model;

public class BatikromUserMsgModel {
    String documentId;
    Object lastupdatetime ;
    int unseen_message ;

    public BatikromUserMsgModel(String documentId, Object lastupdatetime, int unseen_message) {
        this.documentId = documentId;
        this.lastupdatetime = lastupdatetime;
        this.unseen_message = unseen_message;
    }

    public BatikromUserMsgModel() {
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }


    public Object getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(Object lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public int getUnseen_message() {
        return unseen_message;
    }

    public void setUnseen_message(int unseen_message) {
        this.unseen_message = unseen_message;
    }

    @Override
    public String toString() {
        return "BatikromUserMsgModel{" +
                "documentId='" + documentId + '\'' +
                ", lastupdatetime='" + lastupdatetime + '\'' +
                ", unseen_message='" + unseen_message + '\'' +
                '}';
    }
}
