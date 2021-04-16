package com.agamilabs.smartshop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.adapter.OrderReportAdapter;
import com.agamilabs.smartshop.constants.AppConstants;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.CartStatusModel;
import com.agamilabs.smartshop.model.OrderReportModel;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderReportActivity extends AppCompatActivity {
//    private String ORDER_SUMMARY_URL = "http://192.168.1.4/android/AgamiLab/smart_shop/order_summary.php";
    private String ORDER_SUMMARY_URL = "http://ghorket.com/app-module/php/ui/cart/get_filtered_cart_modified.php";


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mOrderRecyclerView;
    private List<OrderReportModel> mOrderList;
    private OrderReportAdapter mOrderAdapter;
    private LinearLayoutManager linearLayoutManager ;
    private int testLast = 0, lengthArray=0;
    private int totalItemCount, pastVisiblesItems,  visibleItemCount, page =1 ;
    private boolean loading = true ;
    private ProgressBar mProgressbar;

    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 5*60*1000 ;

    private int pageNumber = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_report);

        setTitle("Order Summary Details");


        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mProgressbar = findViewById(R.id.progressbar) ;
        mOrderRecyclerView = findViewById(R.id.recycler_order_report) ;


        mOrderList = new ArrayList<>() ;
        initializeAdapter() ;


        loadProducts(pageNumber);

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                cancelAutoRefresh();
                mOrderList.clear();
                loadProducts(1);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        loadNextPage();


    }

    private void initializeAdapter() {
        mOrderAdapter = new OrderReportAdapter(getApplicationContext(), mOrderList);
        mOrderRecyclerView.setAdapter(mOrderAdapter);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false) ;
        mOrderRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void loadProducts(int number) {
        loading = true;
        pageNumber=Math.max(number, 1) ;

        HashMap<String, String> map = new HashMap<>() ;
        map.put("apikey", AppConstants.API_KEY) ;
        map.put("pageno", pageNumber+"") ;
        map.put("cartorderid", "") ;
        AppController.getAppController().getAppNetworkController().makeRequest(ORDER_SUMMARY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppController.getAppController().getInAppNotifier().log("TAG", "response:  "+ response);
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getString("error").equalsIgnoreCase("false")){
                        JSONArray orderSummaryDataArray = object.getJSONArray("data");
                        AppController.getAppController().getInAppNotifier().log("TAG", "orderSummaryDataArray:  "+ orderSummaryDataArray);

                        lengthArray = orderSummaryDataArray.length() ;
                        ForLoadData(orderSummaryDataArray) ; //initial load data

                        mOrderAdapter.notifyDataSetChanged();
                        setAutoRefresh() ;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppController.getAppController().getInAppNotifier().log("TAG", "error: "+ error);
            }
        }, map);
    }
    private void ForLoadData(JSONArray orderArray) {
        AppController.getAppController().getInAppNotifier().log("orderArray", "orderArray:  "+ orderArray);

        for(int i=0; i<orderArray.length(); i++)
        {
            List<CartStatusModel> mCartStatusList = new ArrayList<>();
            testLast = i ;
            JSONObject mSummaryDataObject = null;
            try {
                mSummaryDataObject = orderArray.getJSONObject(i);
                AppController.getAppController().getInAppNotifier().log("mSummaryDataObject", "mSummaryDataObject:  "+ mSummaryDataObject);

                JSONArray mCartArray = mSummaryDataObject.getJSONArray("cart_status") ;
                AppController.getAppController().getInAppNotifier().log("mCartArray", "mCartArray:  "+ mCartArray);

                for (int j=0 ; j<mCartArray.length(); j++){
                    JSONObject mCartObject = mCartArray.getJSONObject(j) ;
                    AppController.getAppController().getInAppNotifier().log("mCartObject", "mCartObject:  "+ mCartObject);
                    AppController.getAppController().getInAppNotifier().log("mCartObject2", "mCartObject:  "+ mCartObject.getString("cstatusno"));
                    AppController.getAppController().getInAppNotifier().log("mCartObject2", "mCartObject:  "+ mCartObject.getString("statustitle"));
                    AppController.getAppController().getInAppNotifier().log("mCartObject2", "mCartObject:  "+ mCartObject.getString("icon"));
                    AppController.getAppController().getInAppNotifier().log("mCartObject2", "mCartObject:  "+ mCartObject.getString("is_end"));
                    AppController.getAppController().getInAppNotifier().log("mCartObject2", "mCartObject:  "+ mCartObject.getString("statustime"));
                    AppController.getAppController().getInAppNotifier().log("mCartObject2", "mCartObject:  "+ mCartObject.getString("passed"));

                    mCartStatusList.add(new CartStatusModel(
                            mCartObject.getString("cstatusno"),
                            mCartObject.getString("statustitle"),
                            mCartObject.getString("icon"),
                            mCartObject.getString("is_end"),
                            mCartObject.getString("statustime"),
                            mCartObject.getString("passed")
                    )) ;
                    AppController.getAppController().getInAppNotifier().log("mCartStatusList1", "mCartStatusList:  "+ mCartStatusList);

                }
                AppController.getAppController().getInAppNotifier().log("mCartStatusList2", "mCartStatusList:  "+ mCartStatusList);
                AppController.getAppController().getInAppNotifier().log("mSummaryDataObject2",
                        "mSummaryDataObject:  "+ mSummaryDataObject.getString("forstreet"));

                mOrderList.add(new OrderReportModel(
                        mSummaryDataObject.getString("forstreet"),
                        mSummaryDataObject.getString("forcity"),
                        mSummaryDataObject.getString("forpostcode"),
                        mSummaryDataObject.getString("forcontact"),
                        mSummaryDataObject.getString("cartdatetime"),
                        mSummaryDataObject.getString("delivarydatetime"),
                        mSummaryDataObject.getString("cartorderid"),
                        mSummaryDataObject.getString("ufirstname"),
                        mSummaryDataObject.getString("ulastname"),
                        mCartStatusList
                ));

//                String delivaryDateTime = mSummaryDataObject.getString("delivarydatetime") ;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        AppController.getAppController().getInAppNotifier().log("mOrderList", "mOrderList:  "+ mOrderList);


    }
    private void loadNextPage() {
        mOrderRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                {

                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount-1) {
                            loading = false;
                            mProgressbar.setVisibility(View.VISIBLE);

                            setAutoRefreshPagination();
                            mOrderAdapter.notifyDataSetChanged();


                        }
                    }
                }
            }
        });
    }

    private void setAutoRefresh(){
        //if already countdowntime nul na hole, countdowntimer k stop korbe.
        //max refresh time 0 or 0 theke chotu hoi, uporer kaj ta korbe..
        if(remainingRefreshTime<=0){
            if(countDownTimer!= null){
                countDownTimer.cancel();
            }
            return;

        }
        if(countDownTimer == null){
            countDownTimer = new CountDownTimer(remainingRefreshTime, 5*1000) {
                @Override
                public void onTick(long millisUntilFinished) {
//                    AppController.getAppController().getInAppNotifier().log("Thick", "onTrick");
                    mOrderAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFinish() {
//                    AppController.getAppController().getInAppNotifier().log("Finish", "onFinish");
                }
            };

            countDownTimer.start() ;
        }
    }
    private void cancelAutoRefresh(){
    if(countDownTimer!= null){
        countDownTimer.cancel();
        countDownTimer=null;
    }
}

    private void setAutoRefreshPagination(){


        if(countDownTimer == null){
            countDownTimer = new CountDownTimer(1000, 200) {
                @Override
                public void onTick(long millisUntilFinished) {
                    AppController.getAppController().getInAppNotifier().log("recycler", "onTrick");
                }

                @Override
                public void onFinish() {
                    AppController.getAppController().getInAppNotifier().log("recycler", "onFinish");
                    int j = pageNumber + 1;
                    loadProducts(j);
                    mProgressbar.setVisibility(View.GONE);
                    cancelAutoRefresh();
                }
            };

            countDownTimer.start() ;
        }
    }

}