package com.agamilabs.smartshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener {
    private EditText editText_host_url, editText_domain_name, editText_user_name, editText_password;
    private Button btn_submit;
    private String url = "/connector/index.php";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestQueue = Volley.newRequestQueue(this);

        editText_host_url = findViewById(R.id.login_hosturl);
        editText_domain_name = findViewById(R.id.login_domain_name);
        editText_user_name = findViewById(R.id.login_username);
        editText_password = findViewById(R.id.login_password);

        btn_submit = findViewById(R.id.login_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //activity
                startActivity(new Intent(getApplicationContext(), ShopAdminHome.class ));



                final HashMap<String, String> params = new HashMap<>();

                String host_url = editText_host_url.getText().toString();
                String domain_name = editText_domain_name.getText().toString();
                params.put("domain_name", domain_name);
                params.put("userid", editText_user_name.getText().toString());
                params.put("password", editText_password.getText().toString());

                Log.e("TAG", "prams map:  "+ params ) ;

                String host = "" + host_url + url;

                StringRequest stringRequest = new StringRequest(Request.Method.POST, host,
                        LoginActivity.this, LoginActivity.this) {
                    @Override
                    protected Map<String, String> getParams() {
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });


    }


    @Override
    public void onResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            if (object.has("error") && !object.getBoolean("error")) {

                JSONArray topicsArray = object.getJSONArray("topics");
                ArrayList<String> topics = new ArrayList<>();
                for (int i = 0; i < topicsArray.length(); i++) {
                    JSONObject aTopic = topicsArray.getJSONObject(i);
                    String shop_name = aTopic.getString("shop_name");
                    String topic_name = aTopic.getString("topic_name");
                    String auth_domain = aTopic.getString("auth_domain");
                    String auth_key = aTopic.getString("auth_key");

                    FirebaseMessaging.getInstance().subscribeToTopic(topic_name);

                    // BEGIN[save topics in database]

                    // END[save topics in database]
                }
            } else {
                Toast.makeText(this, object.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();

    }
}