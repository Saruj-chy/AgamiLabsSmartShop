package com.agamilabs.smartshop.model;

import java.lang.reflect.Field;

public class CategoryStockReportModel {

    public String catid, cattext, parentcatid ;

    public CategoryStockReportModel() {
    }

    public CategoryStockReportModel(String cattext) {
        this.cattext = cattext;
    }

    public Field[] getAllFields(){
        return this.getClass().getDeclaredFields() ;
    }

    public String getCatid() {
        return catid;
    }

    public String getCattext() {
        return cattext;
    }

    public String getParentcatid() {
        return parentcatid;
    }
}
