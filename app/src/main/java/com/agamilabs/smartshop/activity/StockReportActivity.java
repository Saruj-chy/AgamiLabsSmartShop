package com.agamilabs.smartshop.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.adapter.StockReportAdapter;
import com.agamilabs.smartshop.model.StockReportModel;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StockReportActivity extends AppCompatActivity {

    private String STOCK_URL = "http://192.168.1.5/android/AgamiLab/smart_shop/stock.json";
    private String CATEGORY_URL = "http://192.168.1.5/android/AgamiLab/smart_shop/category.json";
//    private String STOCK_URL = "http://192.168.1.5/android/AgamiLab/agami-logbook/view_section.php";


    private RecyclerView mStockRecyclerView;
    private List<StockReportModel> mStockList;

    private StockReportAdapter mStockAdapter ;
    CardView mCardView ;
    TextInputEditText mProductEdit;
    TextView mDateFrom, mDateTo;
    Button  mFilterBtn ;
    TextView mCategoryTextView;
    Spinner mReorderSpin ;
    LinearLayout mDateFromLinear, mDateToLinear ;
    ImageButton mImgBtnDF, mImgBtnDT;

    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog picker;

    //popup dialog
    TextInputEditText mSearchEditext ;
    ListView mSearchListView ;

    private AlertDialog.Builder dialogBuilder  ;
    private AlertDialog dialog;
    List<String> categoryList = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);
        setTitle("Stock Report");

        mCardView=findViewById(R.id.cardView_stock_report) ;
        mProductEdit = findViewById(R.id.edit_product_name);
        mCategoryTextView = findViewById(R.id.text_category);
        mReorderSpin = findViewById(R.id.spinner_reorder_point);
        mDateFromLinear = findViewById(R.id.linear_date_from);
        mDateToLinear = findViewById(R.id.linear_date_to);
        mDateFrom = findViewById(R.id.text_date_from);
        mDateTo = findViewById(R.id.text_date_to);
        mFilterBtn = findViewById(R.id.btn_filter);
        mImgBtnDF = findViewById(R.id.imagebtn_date_from);
        mImgBtnDT = findViewById(R.id.imagebtn_date_to);

        dialogBuilder = new AlertDialog.Builder(this);
        mStockRecyclerView = findViewById(R.id.recycler_stock_report) ;
        mStockRecyclerView.setHasFixedSize(true);
        mStockRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));



        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
//        showDate(mDateFrom,year, month+1, day);
//        showDate(mDateTo,year, month+1, day);


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
        loadProducts("","","","","");
        loadCategory(false);

    }

    private void createPopupDialog()
    {
        View view = getLayoutInflater().inflate(R.layout.layout_popup, null);
       mSearchEditext = view.findViewById(R.id.inputedit_popup_category) ;
       mSearchListView = view.findViewById(R.id.listview_popup) ;


        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        loadCategory(true) ;

        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCategoryTextView.setText((String) parent.getItemAtPosition(position));
               dialog.cancel();
            }
        });

//        submitBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (!editEmployName.getText().toString().isEmpty() && !editEmployPosition.getText().toString().isEmpty()
//                        && !editEmployContact.getText().toString().isEmpty() && !editEmployWebpage.getText().toString().isEmpty()
//                        && !editEmployEmail.getText().toString().isEmpty() && !editEmployAdress.getText().toString().isEmpty()
//                ) {
//
//                    employName = editEmployName.getText().toString().trim();
//                    employPosition = editEmployPosition.getText().toString().trim();
//                    employContact = editEmployContact.getText().toString().trim();
//                    employWebpage = editEmployWebpage.getText().toString().trim();
//                    employEmail = editEmployEmail.getText().toString().trim();
//                    employAdress = editEmployAdress.getText().toString().trim();
//
//                    db.insertUserInfo(employName, employPosition, employContact, employWebpage, employEmail, employAdress);
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            dialog.dismiss();
//
////                            Intent intent = new Intent(getApplicationContext(), EmployListActivity.class);
////                            startActivity(intent);
//                            onIntent(getApplicationContext(),EmployListActivity.class);
//                            finish();
//                        }
//                    }, 1000); //  1 second.
//                }
//
//            }
//        });
    }



    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
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
                    showDate(mDateFrom,arg1, arg2+1, arg3);
                }
            };
    private DatePickerDialog.OnDateSetListener myDateListener2 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(mDateTo,arg1, arg2+1, arg3);
                }
            };

    private void showDate(TextView textView, int year, int month, int day) {
        textView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }



    private void loadProducts(String item_name, String category_name, String reorder_point, String date_from, String date_to) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, STOCK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "response123: "+ response) ;
                        mCardView.setVisibility(View.VISIBLE);

                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray sectionArray = object.getJSONArray("report");

                          for(int i=0;i<sectionArray.length();i++){
                                JSONObject mNavigateObject = sectionArray.getJSONObject(i);
                                StockReportModel aNavigationModel = new StockReportModel() ;
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
                                mStockList.add(aNavigationModel);

                            }
                            Log.e("TAG", "mStockList: "+ mStockList) ;

                            mStockAdapter = new StockReportAdapter(getApplicationContext(), mStockList);
                            mStockRecyclerView.setAdapter(mStockAdapter);
                            GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
                            mStockRecyclerView.setLayoutManager(manager);
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
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> parameters = new HashMap<String, String>();
//                parameters.put("item_name",  item_name );
//                parameters.put("category_name",  category_name );
//                parameters.put("reorder_point",  reorder_point );
//                parameters.put("date_from",  date_from );
//                parameters.put("date_to",  date_to );
//                return parameters;
//            }
//
//        };


        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    private void loadCategory(boolean dialogClick) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, CATEGORY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", "response123: "+ response) ;

                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray sectionArray = object.getJSONArray("category");

                            for(int i=0;i<sectionArray.length();i++){
                                JSONObject mNavigateObject = sectionArray.getJSONObject(i);

                                Log.e("TAG", "category name: "+ mNavigateObject.getString("itemname")) ;

                                categoryList.add(mNavigateObject.getString("itemname")) ;

                            }
                            Log.e("TAG", "mStockList: "+ mStockList) ;

                            if(dialogClick==true){
                                ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, categoryList);
                                mSearchListView.setAdapter(adapter);
                            }else{
                                mCategoryTextView.setText(categoryList.get(0));
                            }


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
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> parameters = new HashMap<String, String>();
//                parameters.put("item_name",  item_name );
//                parameters.put("category_name",  category_name );
//                parameters.put("reorder_point",  reorder_point );
//                parameters.put("date_from",  date_from );
//                parameters.put("date_to",  date_to );
//                return parameters;
//            }
//
//        };


        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


}