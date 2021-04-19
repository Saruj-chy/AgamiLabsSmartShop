package com.agamilabs.smartshop.aaaaaaaaaa;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.agamilabs.smartshop.controller.AppController;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.List;

public class MyApplication {

    private CookieManager mCookieManager = null;

    MyApplication() {
        mCookieManager = new CookieManager();
        mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(mCookieManager);
    }

    private List<HttpCookie> getCookies() {
        if(mCookieManager == null){
            return null;
        }
        else{
            AppController.getAppController().getInAppNotifier().log("cookie", "get mCookieManager: "+mCookieManager );
            AppController.getAppController().getInAppNotifier().log("cookie", "get cookieStore: "+mCookieManager.getCookieStore() );

            return mCookieManager.getCookieStore().getCookies();
        }
    }

    public void clearCookies() {
        if(mCookieManager != null)
            mCookieManager.getCookieStore().removeAll();
    }

    public boolean isCookieManagerEmpty() {
        if(mCookieManager == null)
            return true;
        else
            return mCookieManager.getCookieStore().getCookies().isEmpty();
    }


    public String getCookieValue() {
        String cookieValue = new String();

        if(!isCookieManagerEmpty()) {
            for (HttpCookie eachCookie : getCookies())
            {
                cookieValue = cookieValue + String.format("%s=%s", eachCookie.getName(), eachCookie.getValue() );
                AppController.getAppController().getInAppNotifier().log("cookie", "eachCookie: "+eachCookie);
                AppController.getAppController().getInAppNotifier().log("cookie", "cookieValue: "+cookieValue.toString().trim());
            }
        }
        return cookieValue;
    }


}
