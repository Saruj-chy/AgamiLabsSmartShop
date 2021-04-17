package com.agamilabs.smartshop.FireInboxShow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatiUserChatsModel {
    public ArrayList<String> content;
    public String number;

    public BatiUserChatsModel(String s, String number) {
    }

    public BatiUserChatsModel(ArrayList<String> content, String number) {
        this.content = content;
        this.number = number;
    }

    public ArrayList<String> getContent() {
        return content;
    }

    public void setContent(ArrayList<String> content) {
        this.content = content;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
