package com.agamilabs.smartshop.model;

import java.lang.reflect.Field;

public class OrderReportModel {

    public String cartno, storeno, userno, cartorderid;
    public  String forstreet, forcity, forpostcode, forcontact ;
    public String cartdatetime, delivarydatetime ;
    public String ufirstname, ulastname ;

    public OrderReportModel() {
    }

    public Field[] getAllFields(){
        return this.getClass().getDeclaredFields() ;
    }


    public String getCartno() {
        return cartno;
    }

    public String getStoreno() {
        return storeno;
    }

    public String getUserno() {
        return userno;
    }

    public String getCartorderid() {
        return cartorderid;
    }

    public String getForstreet() {
        return forstreet;
    }

    public String getForcity() {
        return forcity;
    }

    public String getForpostcode() {
        return forpostcode;
    }

    public String getForcontact() {
        return forcontact;
    }

    public String getCartdatetime() {
        return cartdatetime;
    }

    public String getDelivarydatetime() {
        return delivarydatetime;
    }

    public String getUfirstname() {
        return ufirstname;
    }

    public String getUlastname() {
        return ulastname;
    }

    @Override
    public String toString() {
        return "OrderReportModel{" +
                "cartno='" + cartno + '\'' +
                ", storeno='" + storeno + '\'' +
                ", userno='" + userno + '\'' +
                ", cartorderid='" + cartorderid + '\'' +
                ", forstreet='" + forstreet + '\'' +
                ", forcity='" + forcity + '\'' +
                ", forpostcode='" + forpostcode + '\'' +
                ", forcontact='" + forcontact + '\'' +
                ", cartdatetime='" + cartdatetime + '\'' +
                ", delivarydatetime='" + delivarydatetime + '\'' +
                '}';
    }
}
