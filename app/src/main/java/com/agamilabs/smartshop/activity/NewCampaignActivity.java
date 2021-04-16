package com.agamilabs.smartshop.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.agamilabs.smartshop.LoginActivity;
import com.agamilabs.smartshop.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewCampaignActivity extends AppCompatActivity
        implements View.OnClickListener, Response.Listener<String>, Response.ErrorListener {
    private ExpandableRelativeLayout expandableLayout;
    private Button btn_expand,btn_save;
    private ImageButton imageBtn_DP,imageBtn_TP;
    private TextView txtDate,txtTime,txt_count_valid_num;
    private EditText editText_recipients,edittext_campaignName,edittext_message,editText_extra_recipients;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private ArrayList<String> phoneNumList = new ArrayList<String>();
    private String url = "/connector/index5.php";
    private String isChecked="false",format,selectTime,selectDate,monthName;
    RequestQueue requestQueue,requestQueueDuplicate;
    public static NewCampaignActivity INSTANCE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_campaign);

        INSTANCE = this;
        init();

        loadDateTime();

        requestQueue = Volley.newRequestQueue(this);
        requestQueueDuplicate = Volley.newRequestQueue(this);

        btn_expand.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        imageBtn_DP.setOnClickListener(this);
        imageBtn_TP.setOnClickListener(this);

        editText_recipients.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editText_recipients.getText().toString();
                String[] array = str.split(",");
                int count = 0;

                for(int i=0;i<array.length;i++){
                    if(array[i].trim().length()==11){
                        String newStr = array[i];
                        boolean check=true;
                        for(int j=0;j<newStr.length();j++){
                            char ch = newStr.charAt(j);
                            Log.d("value",Integer.valueOf(ch).toString());
                            if(Integer.valueOf(ch)<48 || Integer.valueOf(ch)>57){
                                check=false;
                                break;
                            }
                        }
                        if(check){
                            count++;
                            txt_count_valid_num.setText(String.valueOf(count));
                            txt_count_valid_num.setTextColor(Color.BLACK);
                        }
                        else {
                            txt_count_valid_num.setText("Please enter valid phone number");
                            txt_count_valid_num.setTextColor(Color.RED);
                        }
                    }
                    else if(str.length()>0 && str.charAt(str.length()-1) == ','){
                        txt_count_valid_num.setText("Please enter valid phone number");
                        txt_count_valid_num.setTextColor(Color.RED);
                    }
                    else{
                        txt_count_valid_num.setText(String.valueOf(count));
                        txt_count_valid_num.setTextColor(Color.BLACK);
                    }
                }
            }
        });

        setTitle("NewCampaign");
    }

    public static NewCampaignActivity getActivityInstance()
    {
        return INSTANCE;
    }

    public void sendDuplicateInfo(String schedule_no, String message_title, String message, String catid, String session, String apiid, String schedule_starting_datetime, String schedule_particular_number, String priority, String status, String schedule_entry_datetime,
                                  String total_subscribers, String total_sent_subscribers, String total_cost){
        final HashMap<String, String> params = new HashMap<>();

        params.put("campaign_name",message_title);
        params.put("recipients", "");
        params.put("message", message);
        params.put("campaign_start_date_time", schedule_starting_datetime);
        params.put("api_id", apiid);
        params.put("exttra_recipient", "");
        params.put("saveAsTemplate","");

        Log.d("Total",params.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                NewCampaignActivity.this, NewCampaignActivity.this) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        requestQueueDuplicate.add(stringRequest);
    }

    private void loadDateTime() {
        //default value for date and time
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).substring(0,3);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        if (mHour == 0) {
            mHour += 12;
            format = "AM";
        } else if (mHour == 12) {
            format = "PM";
        } else if (mHour > 12) {
            mHour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        txtDate.setText(mDay + " " + (monthName) + ", " + mYear);
        txtTime.setText(mHour + ":" + mMinute+" "+format);
    }

    private void init() {
        expandableLayout = findViewById(R.id.expandableLayout);
        btn_expand = findViewById(R.id.expandableButton);
        btn_save = findViewById(R.id.btn_save);
        imageBtn_DP = findViewById(R.id.imagebtn_datePicker);
        imageBtn_TP = findViewById(R.id.imagebtn_TimePicker);
        txtDate = findViewById(R.id.text_date);
        txtTime = findViewById(R.id.text_time);
        txt_count_valid_num = findViewById(R.id.text_count_valid_num);
        edittext_campaignName = findViewById(R.id.edittext_campaignName);
        editText_recipients = findViewById(R.id.edittext_recipients);
        edittext_message = findViewById(R.id.edittext_message);
        editText_extra_recipients = findViewById(R.id.edittext_extra_recipients);
        radioGroup = findViewById(R.id.radioGroup);

    }

    @Override
    public void onClick(View view) {
        if(view == btn_expand){
            expandableLayout.toggle();
        }
        else if(view == imageBtn_DP){
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            String monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).substring(0,3);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            selectDate = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;

                            txtDate.setText(dayOfMonth + " " + (monthName) + ", " + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        else if(view == imageBtn_TP){
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            selectTime = hourOfDay + ":" + minute+":"+"00";

                            if (hourOfDay == 0) {
                                hourOfDay += 12;
                                format = "AM";
                            } else if (hourOfDay == 12) {
                                format = "PM";
                            } else if (hourOfDay > 12) {
                                hourOfDay -= 12;
                                format = "PM";
                            } else {
                                format = "AM";
                            }

                            txtTime.setText(hourOfDay + ":" + minute+" "+format);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        else if(view == btn_save){

            loadArrayList();

            int selectedId = radioGroup.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);

            final HashMap<String, String> params = new HashMap<>();

            params.put("campaign_name",edittext_campaignName.getText().toString());
            params.put("recipients", phoneNumList.toString());
            params.put("message", edittext_message.getText().toString());
            params.put("campaign_start_date_time", selectDate+" "+selectTime);
            params.put("api_id", radioButton.getText().toString());
            params.put("exttra_recipient", editText_extra_recipients.getText().toString());
            params.put("saveAsTemplate",isChecked);

            Log.d("Total",params.toString());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    NewCampaignActivity.this, NewCampaignActivity.this) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }

    private void loadArrayList() {
        String str = editText_recipients.getText().toString();
        String[] array = str.split(",");

        for(int i=0;i<array.length;i++){
            if(array[i].trim().length()==11){
                String newStr = array[i];
                boolean check=true;
                for(int j=0;j<newStr.length();j++){
                    char ch = newStr.charAt(j);
                    Log.d("value",Integer.valueOf(ch).toString());
                    if(Integer.valueOf(ch)<48 || Integer.valueOf(ch)>57){
                        check=false;
                        break;
                    }
                }
                if(check){
                    //if element does not exists in the ArrayList, add it
                    if(!phoneNumList.contains(newStr)){
                        phoneNumList.add(newStr);
                    }
                }
            }
            Log.d("List",phoneNumList.toString());
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(String response) {
        try {
            JSONObject object = new JSONObject(response);
            if (object.has("error") && !object.getBoolean("error")) {

                startActivity(new Intent(NewCampaignActivity.this,CampaignActivity.class));

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(object.getString("message"));
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Dismiss",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

        }
    }

    public void itemClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()){
           isChecked="true";
        }
    }
}