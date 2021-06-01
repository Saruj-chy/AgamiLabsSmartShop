package com.agamilabs.smartshop.FireInboxShow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatiChatsModal {
    String userChatId ;
    List<String> usersList  ;

    public BatiChatsModal(String userChatId, List<String> usersList) {
        this.userChatId = userChatId;
        this.usersList = usersList;
    }


    public String getUserChatId() {
        return userChatId;
    }

    public void setUserChatId(String userChatId) {
        this.userChatId = userChatId;
    }

    public List<String> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<String> usersList) {
        this.usersList = usersList;
    }



    @Override
    public String toString() {
        return "BatiChatsModal{" +
                "userChatId='" + userChatId + '\'' +
                ", usersList=" + usersList +
                '}';
    }
}
