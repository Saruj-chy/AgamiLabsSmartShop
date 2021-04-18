package com.agamilabs.smartshop.FireInboxShow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatiChatsModal {
    String userChatId, userChats1, userChats2 ;
    List<String> usersList  ;

    public BatiChatsModal(String id, String s) {
    }

    public BatiChatsModal(String userChatId, List<String> usersList) {
        this.userChatId = userChatId;
        this.usersList = usersList;
    }

    public BatiChatsModal(String userChatId, String userChats1, String userChats2) {
        this.userChatId = userChatId;
        this.userChats1 = userChats1;
        this.userChats2 = userChats2;
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

    public String getUserChats1() {
        return userChats1;
    }

    public void setUserChats1(String userChats1) {
        this.userChats1 = userChats1;
    }

    public String getUserChats2() {
        return userChats2;
    }

    public void setUserChats2(String userChats2) {
        this.userChats2 = userChats2;
    }
}
