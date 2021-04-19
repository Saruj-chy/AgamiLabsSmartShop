package com.agamilabs.smartshop.aaaaaaaaaa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.agamilabs.smartshop.R;

public class NetworkConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_connect);

       if(CheckWifiConnection()){
           Toast.makeText(this, "Network is Connected", Toast.LENGTH_SHORT).show();

       }else{
           Toast.makeText(this, "Network is Disconnected.", Toast.LENGTH_SHORT).show();
       }

    }

    public boolean CheckWifiConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}