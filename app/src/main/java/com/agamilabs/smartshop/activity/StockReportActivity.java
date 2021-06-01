package com.agamilabs.smartshop.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.agamilabs.smartshop.Interfaces.OnClickInterface;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.adapter.CategoryStockReportAdapter;
import com.agamilabs.smartshop.adapter.StockReportAdapter;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.CategoryStockReportModel;
import com.agamilabs.smartshop.model.StockReportModel;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class StockReportActivity extends AppCompatActivity implements OnClickInterface  {

//    private String STOCK_URL = "http://192.168.1.6/android/AgamiLab/smart_shop/stock_report.php";
    private String STOCK_URL = "http://pharmacy.egkroy.com/app-module/php/get_stock_report.php";
//    private String CATEGORY_URL = "http://192.168.1.6/android/AgamiLab/smart_shop/category.php";
    private String CATEGORY_URL = "http://pharmacy.egkroy.com/app-module/php/get_item_category.php";

    String apikey = "ewfw?f23u#rfg3872r23=jrfg87wefc" ;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NestedScrollView mNestedScroll ;
    private RecyclerView mStockRecyclerView ;
    private TextInputEditText mProductEdit;
    private TextView mDateFromTextView, mDateToTextView;
    private Button  mFilterBtn ;
    private TextView mCategoryTextView;
    private Spinner mReorderSpin ;
    private ImageButton mImgBtnDF, mImgBtnDT ;
    private RelativeLayout mRelativeLayout;
    private ShimmerFrameLayout shimmerFrameLayout;


    private List<StockReportModel> mStockList;
    private List<CategoryStockReportModel> mCategoryList  ;

    private StockReportAdapter mStockAdapter ;


    private Calendar calendar;
    private int year, month, day;
    private String mFromDate, mToDate ;

    //popup dialog
    private TextInputEditText mSearchEditext ;
    private RecyclerView mSearchRecyclerView ;
    private ArrayAdapter adapter ;
    private AlertDialog.Builder dialogBuilder  ;
    private AlertDialog dialog;
    private ArrayList<String> categoryArrayList ;


    private int pageNumb = 1, lengthArray=0;
    private ProgressBar mProgressbar;

    private CategoryStockReportAdapter mCategoryAdapter;
    private LinearLayoutManager linearLayoutManager ;


    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 1000 ;

    String mItemName, mCategoryId, mReorderId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);
        setTitle("Stock Report");

        initialize() ;

        dialogBuilder = new AlertDialog.Builder(this);
        categoryArrayList = new ArrayList<>() ;

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener myDateListener = null;
        mImgBtnDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);

            }
        });
        mImgBtnDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(2);

            }
        });
        mCategoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopupDialog();
            }
        });

        mStockList = new ArrayList<>() ;
        mCategoryList = new ArrayList<>() ;
        initializeAdapter();

        loadCategoryList(false);
        loadStockReportList(pageNumb, "", "", "", "", "");
        loadNextPageList();

        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.RED,Color.DKGRAY);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setAutoRefresh();
            }
        });
        //search product
        SearchProductText();

        mFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemName = mProductEdit.getText().toString().trim() ;
                mCategoryId = mCategoryTextView.getTag()+"" ;
                mReorderId = mReorderSpin.getSelectedItemId()+"" ;

                loadStockReportList(pageNumb, mItemName, mCategoryId, mReorderId, mFromDate, mToDate);
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
            countDownTimer = new CountDownTimer(remainingRefreshTime, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mStockList.clear();
                    mCategoryList.clear();
                    mProductEdit.setText("");
                    pageNumb =1;

                    //shimmer effect
                    mRelativeLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmer();
                }

                @Override
                public void onFinish() {
                    initializeAdapter();
                    loadStockReportList(pageNumb, "", "", "", "", "");
//                    loadNextPageList();
                    loadCategoryList(false);
                    mRelativeLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.setVisibility(View.GONE);
                    shimmerFrameLayout.stopShimmer();

                    mSwipeRefreshLayout.setRefreshing(false);
                    cancelAutoRefresh() ;

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


    private void loadStockReportList(int number, String mItemName, String mCategoryId, String mReorderId, String mFromDate, String mToDate) {
        pageNumb =Math.max(number, 1) ;
        HashMap<String, String> map = new HashMap<>() ;
        map.put("apikey", apikey) ;
        map.put("page", pageNumb +"") ;
        map.put("itemname", mItemName ) ;
        map.put("reorder", mReorderId ) ;
        map.put("catno", mCategoryId ) ;

        if(mFromDate.equalsIgnoreCase("") || mToDate.equalsIgnoreCase("")  ){

        }else{
            map.put("startdate", mFromDate ) ;
            map.put("enddate", mToDate ) ;
        }


        AppController.getAppController().getAppNetworkController().makeRequest(STOCK_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                AppController.getAppController().getInAppNotifier().log("response", response);
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getString("error").equalsIgnoreCase("false")){
                        JSONObject stockArray = object.getJSONObject("data");
                        JSONArray reportArray = stockArray.getJSONArray("report") ;

                        mRelativeLayout.setVisibility(View.VISIBLE);
                        lengthArray = reportArray.length() ;
                        ForLoadData(reportArray);
                        mStockAdapter.notifyDataSetChanged();

                    }
                    else{
                        mRelativeLayout.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, map );


    }
    private void ForLoadData(JSONArray stockArray) {
        for(int i=0; i<stockArray.length(); i++){
            JSONObject mStockObject = null;
            try {
                mStockObject = stockArray.getJSONObject(i);
                StockReportModel aStockModel = new StockReportModel() ;
                Field[] fields =  aStockModel.getAllFields() ;

                for(int j=0; j<fields.length; j++ ){
                    String fieldName = fields[j].getName() ;
                    String fieldValueInJson =mStockObject.has(fieldName)? mStockObject.getString(fieldName) : "" ;
                    try{
                        fields[j].set(aStockModel, fieldValueInJson) ;
                    }catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                mStockList.add(aStockModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void loadNextPageList() {
        if (mNestedScroll != null ) {
            mNestedScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    loadStockReportList(pageNumb, "", "", "", "", "");
                }
            });
        }


    }
    private void initializeAdapter(){
        mStockRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        mStockAdapter = new StockReportAdapter(getApplicationContext(), mStockList) {
            @Override
            public void loadNextPage(int pageNumber) {
                pageNumb = pageNumber ;
                mStockAdapter.setPageNumber(pageNumber);
                AppController.getAppController().getInAppNotifier().showToast("pagenumber: "+pageNumber );
                AppController.getAppController().getInAppNotifier().log("page","pagenumber: "+pageNumber );
            }
        };
        mStockRecyclerView.setAdapter(mStockAdapter);
        mStockRecyclerView.setNestedScrollingEnabled(false);
    }

    private void loadCategoryList(boolean dialogClick) {
        HashMap<String, String> map = new HashMap<>() ;
        map.put("apikey", apikey) ;
        AppController.getAppController().getAppNetworkController().makeRequest(CATEGORY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                AppController.getAppController().getInAppNotifier().log("response", "category response: "+ response );
                try {
                    JSONObject object = new JSONObject(response);
                    if(object.getString("error").equalsIgnoreCase("false")) {
                        JSONArray mStockCategoryArray = object.getJSONArray("data");
//                        AppController.getAppController().getInAppNotifier().log("response", "mStockCategoryArray: "+ mStockCategoryArray );

                        for(int i=0;i<mStockCategoryArray.length();i++){
                            JSONObject mCategoryObject = mStockCategoryArray.getJSONObject(i);
                            CategoryStockReportModel aCategoryModel = new CategoryStockReportModel() ;
                            Field[] fields =  aCategoryModel.getAllFields() ;

                            for(int j=0; j<fields.length; j++ ){
                                String fieldName = fields[j].getName() ;
                                String fieldValueInJson =mCategoryObject.has(fieldName)? mCategoryObject.getString(fieldName) : "" ;
                                try{
                                    fields[j].set(aCategoryModel, fieldValueInJson) ;
                                }catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                            mCategoryList.add(aCategoryModel) ;
                            mCategoryTextView.setText( mCategoryList.get(0).getCattext() ) ;

                            categoryArrayList.add(mCategoryObject.getString("cattext")) ;
                        }

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, map);

    }
    private void createPopupDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_popup, null);
        mSearchEditext = view.findViewById(R.id.inputedit_popup_category) ;
        mSearchRecyclerView = view.findViewById(R.id.recyclerview_popup);

        mCategoryAdapter = new CategoryStockReportAdapter(getApplicationContext(), mCategoryList, this );
        mSearchRecyclerView.setAdapter(mCategoryAdapter);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false) ;
        mSearchRecyclerView.setLayoutManager(linearLayoutManager);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        loadCategoryList(true) ;

        mSearchEditext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchCategoryFilter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //=======     for time
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            return new DatePickerDialog(this,
                    myDateListener1, year, month, day);
        }else if (id == 2) {
            return new DatePickerDialog(this,
                    myDateListener2, year, month, day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener myDateListener1 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
//                    showDate(mDateFrom,arg1, arg2+1, arg3, mFromData);
                    mFromDate = showDate( arg1, arg2+1, arg3);
                    mDateFromTextView.setText(mFromDate);
                }
            };
    private DatePickerDialog.OnDateSetListener myDateListener2 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
//                    showDate(mDateTo,arg1, arg2+1, arg3, mToDate);
                  mToDate =  showDate( arg1, arg2+1, arg3);
                  mDateToTextView.setText(mToDate);
                }
            };
    private String showDate(int year, int month, int day) {
        String date = (new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day))+"" ;

        return  date ;

    }

    //   initialize
    private void initialize() {
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mNestedScroll = findViewById(R.id.nested_scroll_stock);
        mStockRecyclerView = findViewById(R.id.recycler_stock_report) ;
        mProductEdit = findViewById(R.id.edit_product_name);
        mCategoryTextView = findViewById(R.id.text_category);
        mReorderSpin = findViewById(R.id.spinner_reorder_point);
        mDateFromTextView = findViewById(R.id.text_date_from);
        mDateToTextView = findViewById(R.id.text_date_to);
        mFilterBtn = findViewById(R.id.btn_filter);
        mImgBtnDF = findViewById(R.id.imagebtn_date_from);
        mImgBtnDT = findViewById(R.id.imagebtn_date_to);
        mProgressbar = findViewById(R.id.progressbar) ;
        mRelativeLayout = findViewById(R.id.relative_stock_report);
        shimmerFrameLayout = findViewById(R.id.shimmerFrameLayout);
    }
    public void SearchProductText(){
        mProductEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchNameFilter(s.toString());
            }
        });
    }
    private void searchNameFilter(String text) {
        List<StockReportModel> filteredList = new ArrayList<>();

        for (StockReportModel item : mStockList) {

            if (item.getItemname().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
            /*if(item.getTitle().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }*/
        }

        mStockAdapter.searchFilterList(filteredList);
    }
    private void searchCategoryFilter(String text) {
        List<CategoryStockReportModel> filteredList = new ArrayList<>();

        for (CategoryStockReportModel item : mCategoryList) {

            if (item.getCattext().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
            /*if(item.getTitle().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }*/
        }

        mCategoryAdapter.searchFilterList(filteredList);
    }

    @Override
    public void onListItemSelected(CategoryStockReportModel categoryStockReportModel, int position, boolean isLongClick) {

        mCategoryTextView.setText(categoryStockReportModel.getCattext());
        mCategoryTextView.setTag(categoryStockReportModel.getCatid());
        dialog.cancel();

    }
}