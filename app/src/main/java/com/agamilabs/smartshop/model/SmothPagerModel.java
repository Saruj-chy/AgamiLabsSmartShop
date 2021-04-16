package com.agamilabs.smartshop.model;

public class SmothPagerModel {

    String name ;
    Integer imageUrl ;

    public SmothPagerModel(String name, Integer imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public SmothPagerModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Integer imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public String toString() {
        return "SmothPagerModel{" +
                "name='" + name + '\'' +
                ", imageUrl=" + imageUrl +
                '}';
    }
}
