package com.agamilabs.smartshop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.adapter.OrderReportAdapter;
import com.agamilabs.smartshop.adapter.StockReportAdapter;
import com.agamilabs.smartshop.model.OrderReportModel;
import com.agamilabs.smartshop.model.StockReportModel;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OrderReportActivity extends AppCompatActivity {
    private String ORDER_URL = "http://192.168.1.3/android/AgamiLab/smart_shop/order_summary.json";


    private RecyclerView mOrderRecyclerView;
    private List<OrderReportModel> mOrderList;

    private OrderReportAdapter mStockAdapter ;

    List<String> categoryList = new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_report);

        setTitle("Order Report");


        mOrderRecyclerView = findViewById(R.id.recycler_order_report) ;
        mOrderRecyclerView.setHasFixedSize(true);
        mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));



        mOrderList = new ArrayList<>() ;
        loadProducts();
    }





    private void loadProducts() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ORDER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "response123: "+ response) ;

                        try {
                            JSONObject object = new JSONObject(response);
                            if(object.getString("error").equalsIgnoreCase("false")){
                                JSONArray sectionArray = object.getJSONArray("data");
                                for(int i=0;i<sectionArray.length();i++)
                                {
                                    JSONObject mNavigateObject = sectionArray.getJSONObject(i);
                                    OrderReportModel aNavigationModel = new OrderReportModel() ;
                                    Field[] fields =  aNavigationModel.getAllFields() ;

                                    for(int j=0; j<fields.length; j++ ){
                                        String fieldName = fields[j].getName() ;
                                        String fieldValueInJson =mNavigateObject.has(fieldName)? mNavigateObject.getString(fieldName) : "" ;
                                        try{
                                            fields[j].set(aNavigationModel, fieldValueInJson) ;
                                        }catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    mOrderList.add(aNavigationModel);
                                }


                            }
                            Log.e("TAG", "mOrderList: "+ mOrderList) ;

                            mStockAdapter = new OrderReportAdapter(getApplicationContext(), mOrderList);
                            mOrderRecyclerView.setAdapter(mStockAdapter);
                            GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
                            mOrderRecyclerView.setLayoutManager(manager);
                            mStockAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


}