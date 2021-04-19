package com.agamilabs.smartshop.aaaaaaaaaa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.database.DatabaseHandler;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;

public class TestingSessionActivity extends AppCompatActivity {
    private String INSERT_URL = "http://192.168.1.6/android/AgamiLab/testing_session/insert.php";
    private String GET_URL = "http://192.168.1.6/android/AgamiLab/testing_session/session_get.php";

    RelativeLayout mRelativeLayout01, mRelativeLayout02;
    EditText mNameEdit, mPasswordEdit ;
    Button mInsertBtn, mGetBtn, mClearBtn, mLogBtn ;
    TextView mTextSession, mTokenNameTextView ;

    String token, mSiteUrl, mExistingToken ;
    DatabaseHandler db = new DatabaseHandler(this);
    String nameToken, passwordToken ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_session);

       Initialize() ;
       mSiteUrl = getIntent().getStringExtra("site_url").toString().trim();
       mExistingToken = db.checkExistingToken(mSiteUrl) ;

       if(mExistingToken!=null){
           mRelativeLayout01.setVisibility(View.GONE);
           mRelativeLayout02.setVisibility(View.VISIBLE);
           getJsonList(mExistingToken);
       }

        AppController.getAppController().getInAppNotifier().log("mSiteUrl", mSiteUrl);
        AppController.getAppController().getInAppNotifier().log("mExistingToken", mExistingToken);


//        setCoockieManager() ;
        MyApplication myApplication = new MyApplication();

        mInsertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertLoginInfo() ;
            }
        });

        mGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGetLoginInfo() ;
            }
        });

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApplication.clearCookies();
            }
        });

        mLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean logState = db.deleteTokenRow(mSiteUrl) ;
                if(logState == true){
                    AppController.getAppController().getInAppNotifier().showToast("LogOut Successfull");
                    mRelativeLayout01.setVisibility(View.VISIBLE);
                    mRelativeLayout02.setVisibility(View.GONE);
                }else{
                    AppController.getAppController().getInAppNotifier().showToast("not logout");

                }

            }
        });
    }

    private void Initialize() {
        mRelativeLayout01 = findViewById(R.id.relative_layout_1);
        mRelativeLayout02 = findViewById(R.id.relative_layout_2);
        mNameEdit = findViewById(R.id.edit_user);
        mPasswordEdit = findViewById(R.id.edit_password);
        mInsertBtn = findViewById(R.id.btn_save);
        mGetBtn = findViewById(R.id.btn_get);
        mClearBtn = findViewById(R.id.btn_clear);
        mLogBtn = findViewById(R.id.btn_log_out);
        mTextSession = findViewById(R.id.text_session);
        mTokenNameTextView = findViewById(R.id.text_token_name);
    }


    private void setCoockieManager() {
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault( manager  );
        String cookies = CookieManager.getDefault().toString();

        AppController.getAppController().getInAppNotifier().log("cookie", "cookie: "+cookies);

    }


    private void loadGetLoginInfo() {
        RequestQueue requestQueue ;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppController.getAppController().getInAppNotifier().log("get", response);
                try {
                    JSONObject object = new JSONObject(response);
//                    AppController.getAppController().getInAppNotifier().log("get", object.getString("name")+"   "+
//                            object.getString("password"));

                    mTextSession.setText("Name:   "+object.getString("name")+"\n Password:   "+ object.getString("password") +
                            "\n Token:   "+ object.getString("token"));
                    token = object.getString("token") ;

                    getJsonList(token);
                    db.insertTokenEncodeDAta(token, mSiteUrl);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "Please check Connection...", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getJsonList(String token) {
        String json = null;
        JSONObject jsonResponse = null;
        try {
//            JWTUtils.decoded(token);
            json = JWTUtils.getJsonObject(token) ;
            jsonResponse = new JSONObject(json);
            AppController.getAppController().getInAppNotifier().log("json",  "jsonResponse: "+ jsonResponse.getString("name"));

            nameToken = jsonResponse.getString("name") ;
            passwordToken = jsonResponse.getString("password") ;

            mTokenNameTextView.setText("Name: "+ nameToken+"  \nPassword:  "+ passwordToken);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertLoginInfo() {
        String name = mNameEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();
        HashMap<String, String> map = new HashMap<>() ;
        map.put("name", name) ;
        map.put("password", password) ;

        AppController.getAppController().getAppNetworkController().makeRequest(INSERT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppController.getAppController().getInAppNotifier().log("insert", "response");
                AppController.getAppController().getInAppNotifier().showToast("insert successfull ");
                mNameEdit.setText("");
                mPasswordEdit.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, map );
    }
}