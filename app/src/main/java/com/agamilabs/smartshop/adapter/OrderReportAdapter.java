package com.agamilabs.smartshop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.SmothScoler;
import com.agamilabs.smartshop.MainActivity;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.activity.OrderReportActivity;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.agamilabs.smartshop.model.CartStatusModel;
import com.agamilabs.smartshop.model.OrderReportModel;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mCtx;
    private List<OrderReportModel> mItemList;
    OrderReportModel mOrderReport ;

    long elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds ;
    int timeNumber;


    public OrderReportAdapter(Context mCtx, List<OrderReportModel> mItemList) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.order_summary_list, null);
        return new OrderReportViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        mOrderReport = mItemList.get(position);
        ((OrderReportViewHolder) holder).bind(mOrderReport, position);
    }



    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class OrderReportViewHolder extends RecyclerView.ViewHolder implements SmothScoler {

        TextView mUserName, mUserAddress, mUserContact, mPostStatus, mOrderId, mDeliveryType, mDeiveryDate, mCountDownTime, mStatusTitle;
        CircleImageView mImageLogo;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        private ImageButton mCallImage ;
        private int totalItemCount, pastVisiblesItems,  visibleItemCount, page =1 ;

        private RecyclerView mTimelineRecycler ;
        private LinearLayoutManager manager ;
        private OrderReportTimelineAdapter mTimelineAdapter ;
        List<CartStatusModel> mCartList = new ArrayList<>() ;

        RecyclerView.SmoothScroller smoothScroller ;

        public OrderReportViewHolder(View itemView) {
            super(itemView);


            mUserName = itemView.findViewById(R.id.text_user_name_order);
            mUserAddress = itemView.findViewById(R.id.text_address_order);
            mUserContact = itemView.findViewById(R.id.text_contact_order);
            mPostStatus = itemView.findViewById(R.id.text_post_status_order);
            mOrderId = itemView.findViewById(R.id.text_id_order);
            mDeliveryType = itemView.findViewById(R.id.text_delivery_type_order);
            mDeiveryDate = itemView.findViewById(R.id.text_delivery_date_order);
            mCountDownTime = itemView.findViewById(R.id.text_countdown_time_order);
            mStatusTitle = itemView.findViewById(R.id.text_status_title_order);
            mImageLogo = itemView.findViewById(R.id.image_logo);
            mCallImage = itemView.findViewById(R.id.image_call_order);
            mTimelineRecycler = itemView.findViewById(R.id.recycler_timeline_order);


            mTimelineAdapter = new OrderReportTimelineAdapter(mCtx, mCartList, this );
            manager = new LinearLayoutManager(mCtx, LinearLayoutManager.HORIZONTAL, false);


             smoothScroller = new LinearSmoothScroller(mCtx){
                @Override
                protected int getHorizontalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };


            mTimelineRecycler.setLayoutManager(manager);  // set horizontal LM
            mTimelineRecycler.setAdapter(mTimelineAdapter);

        }


        @SuppressLint("SetTextI18n")
        public void bind(OrderReportModel mOrderReport, int position) {

            AppController.getAppController().getInAppNotifier().log("orderList", mOrderReport.getmCartStatusList()+"");

//            AppImageLoader.loadImageInView(mOrderReport.pimage, R.drawable.smart_shop_logo, (ImageView)mImageLogo);

            //=========     timelinelist recycler
            List<CartStatusModel> mCartModel = mOrderReport.getmCartStatusList() ;
            mCartList.clear();
            int targetPosition = 0;
            for(int i=0; i<mCartModel.size(); i++){
                this.mCartList.add(this.mCartList.size() , mCartModel.get(i)) ;
                if(mCartModel.get(i).getPassed().equalsIgnoreCase("true")){
                    mStatusTitle.setText(mCartModel.get(i).getStatustitle());
                    targetPosition = i ;
                }
            }
            smoothScroller.setTargetPosition(targetPosition);
            manager.startSmoothScroll(smoothScroller);


            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            if(mOrderReport.getUfirstname().equalsIgnoreCase("") && mOrderReport.getUlastname().equalsIgnoreCase("")){
                mUserName.setText(mOrderReport.getForcontact());
            }else{
                mUserName.setText(mOrderReport.getUfirstname() + " "+ mOrderReport.getUlastname());
            }
            mUserAddress.setText(mOrderReport.getForstreet()+", "+mOrderReport.getForcity()+"-"+mOrderReport.getForpostcode());
//            mUserContact.setText(mOrderReport.getForcontact());

            mOrderId.setText("Order Id: "+ mOrderReport.getCartorderid());
            mDeliveryType.setText("Home Delivery");


            try {
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(simpleDateFormat.parse(mOrderReport.getDelivarydatetime()));
                mDeiveryDate.setText("Due Date: "+currentDateTimeString);
                mDeiveryDate.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mCallImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+ mOrderReport.getForcontact()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
                    mCtx.startActivity(intent);
                }
            });




//          coundown time
//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    try {
                        printPostStatus(mOrderReport.getCartdatetime(), currentDate, currentTime, position);
                        printCountDownTimer( mOrderReport.getDelivarydatetime(), currentDate, currentTime, position);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, 1000);
        }


        public void printPostStatus(String cartdatetime, String currentDate, String currentTime, int position) {
            Date startDate = null;
            Date endDate = null;
            try {
                startDate = simpleDateFormat.parse(cartdatetime);
                endDate = simpleDateFormat.parse(currentDate+" "+currentTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            CalculateTimer(startDate, endDate);

            switch (timeNumber){
                case 0:
                    mPostStatus.setText("Posted a few seconds ago");
                    break;
                case 1:
                    mPostStatus.setText("Posted "+elapsedMinutes+" mins ago");
                    break;
                case 2:
                    mPostStatus.setText("Posted "+elapsedHours+ " hr "+elapsedMinutes+" mins ago");
                    break;
                case 3:
                    mPostStatus.setText("Posted "+elapsedDays+" days "+  elapsedHours+ " hr "+elapsedMinutes+" mins ago");
                    break;
                default:
                    break;
            }


        }
        public void printCountDownTimer(String deliverydatetime, String currentDate, String currentTime, int position) {
            if(deliverydatetime.length() > 0){
                Date startDate = null;
                Date endDate = null;
                try {
                    startDate = simpleDateFormat.parse(currentDate+" "+currentTime);
                    endDate = simpleDateFormat.parse(deliverydatetime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(startDate != null && endDate != null){
                    CalculateTimer(startDate, endDate);
                    switch (timeNumber){
                        case 0:
                            mCountDownTime.setText(" After "+elapsedSeconds +" secs");
                            break;
                        case 1:
                            mCountDownTime.setText("  "+elapsedMinutes+" mins "+ elapsedSeconds +" secs");
                            break;
                        case 2:
                            mCountDownTime.setText("  "+ elapsedHours+ " hr "+elapsedMinutes+" mins "+ elapsedSeconds +" secs");
                            break;
                        case 3:
                            mCountDownTime.setText("  "+elapsedDays+" days "+  elapsedHours+ " hr "+elapsedMinutes+" mins "+ elapsedSeconds +" secs");
                            break;
                        case 4:
                            mCountDownTime.setText(" Delivery time Expired");
                            break;
                        default:
                            break;
                    }
                }




//                notifyItemChanged(position);
//                notifyDataSetChanged();
//                notify();
            }

        }

        public void CalculateTimer ( Date startDate, Date endDate){

            AppController.getAppController().getInAppNotifier().log("start",  startDate.toString());
            AppController.getAppController().getInAppNotifier().log("end", endDate.toString());
            long different = endDate.getTime() - startDate.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            if(different>daysInMilli){
                timeNumber = 3;
            }else if(different>hoursInMilli){
                timeNumber = 2 ;
            } else if(different > minutesInMilli){
                timeNumber = 1 ;
            }else if(different >= 0){
                timeNumber = 0 ;
            } else{
                timeNumber = 4 ;
            }

            elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            elapsedHours =(different / hoursInMilli);
            different = different % hoursInMilli;

            elapsedMinutes = (different / minutesInMilli);
            different = different % minutesInMilli;

            elapsedSeconds = (different / secondsInMilli);
        }


        @Override
        public void SmoothScoller(int position) {
            mTimelineRecycler.getLayoutManager().smoothScrollToPosition(mTimelineRecycler,new RecyclerView.State(),
                    position);
        }
    }




}