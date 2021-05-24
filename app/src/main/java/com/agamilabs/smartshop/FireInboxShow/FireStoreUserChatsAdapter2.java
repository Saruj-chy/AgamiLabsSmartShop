package com.agamilabs.smartshop.FireInboxShow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.FireInboxShow.activity.FirestoreChatImageActivity;
import com.agamilabs.smartshop.FireInboxShow.activity.FirestoreUserChatsActivity;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FireStoreUserChatsAdapter2 extends RecyclerView.Adapter<FireStoreUserChatsAdapter2.FirestoreUserChatsViewHolder> {

    private Context mCtx;
    private List<BatiChatMsgModel> mChatsMsgModalList;

    final static int SENDER_VIEWTYPE_TEXT = 1001 ;
    final static int SENDER_VIEWTYPE_IMAGE = 1002 ;
    final static int SENDER_VIEWTYPE_IMAGE_TEXT = 1003 ;
    final static int RECEIVER_VIEWTYPE_TEXT = 2001 ;
    final static int RECEIVER_VIEWTYPE_IMAGE = 2002 ;
    final static int RECEIVER_VIEWTYPE_IMAGE_TEXT = 2003 ;
    final static int COMMON_DATETEXT = 3001 ;


    HashMap<String, String> mapDateList = new HashMap<>() ;
    HashMap<String, String> mapSentList = new HashMap<>() ;
    HashMap<String, String> mapReceiveList = new HashMap<>() ;

    int dataSize=0, tempDataSize=0 ;
    SharedPreferences sharedPreferences ;
    static String SHARED_PREFS = "admin_store";
    String USER_ID ;

    OnIntentUrl onIntentUrl;
    CharSequence timeFormat, tempTimeFormat=null, dateFormat, tempDateFormat=null ;
    Date date, calenderDate;
    Timestamp timestamp, dateTimestamp ;
    int tempPosition=0;




    public FireStoreUserChatsAdapter2(Context mCtx, List<BatiChatMsgModel> mChatsMsgModalList, OnIntentUrl onIntentUrl) {
        this.mCtx = mCtx;
        this.mChatsMsgModalList = mChatsMsgModalList;
        this.onIntentUrl = onIntentUrl;
        notifyDataSetChanged();

    }

    @Override
    public int getItemViewType(int position) {
        sharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("admin_user_id", "");


        BatiChatMsgModel batiChatMsgModel = mChatsMsgModalList.get(position);
        if(batiChatMsgModel.getSentBy().equalsIgnoreCase(USER_ID)){

            if(!batiChatMsgModel.getMessage().equalsIgnoreCase(null) && batiChatMsgModel.getImageThumbList().size()<=0){
                return SENDER_VIEWTYPE_TEXT;
            }else if(batiChatMsgModel.getMessage().equalsIgnoreCase("") && batiChatMsgModel.getImageThumbList().size()>0){
                return SENDER_VIEWTYPE_IMAGE ;
            }else if(!batiChatMsgModel.getMessage().equalsIgnoreCase("") && batiChatMsgModel.getImageThumbList().size()>0 ){
                return SENDER_VIEWTYPE_IMAGE_TEXT ;
            }
        }else {
            if(batiChatMsgModel.getImageThumbList().size()<=0 && !batiChatMsgModel.getMessage().equalsIgnoreCase("")){
                return RECEIVER_VIEWTYPE_TEXT ;
            }else if(batiChatMsgModel.getImageThumbList().size()>0 && batiChatMsgModel.getMessage().equalsIgnoreCase("")){
                return RECEIVER_VIEWTYPE_IMAGE ;
            }else if(batiChatMsgModel.getImageThumbList().size()>0 && !batiChatMsgModel.getMessage().equalsIgnoreCase("")){
                return RECEIVER_VIEWTYPE_IMAGE_TEXT ;
            }
        }
        Log.e("viewType","viewType0:"+super.getItemViewType(position) +" pos:"+position+" msg:"+batiChatMsgModel.getMessage()+" size:"+batiChatMsgModel.getImageThumbList().size() ) ;








        return super.getItemViewType(position);
    }



    @Override
    public FirestoreUserChatsViewHolder onCreateViewHolder( ViewGroup viewGroup, int viewtype) {
        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);
        View view;


        switch (viewtype){
            case SENDER_VIEWTYPE_TEXT:
                view = layoutInflater.inflate(R.layout.layout_sender_text,viewGroup,false);
                break;
            case SENDER_VIEWTYPE_IMAGE:
                view = layoutInflater.inflate(R.layout.layout_sender_image,viewGroup,false);
                break;
            case SENDER_VIEWTYPE_IMAGE_TEXT:
                view = layoutInflater.inflate(R.layout.layout_sender_imagetext,viewGroup,false);
                break;
            case RECEIVER_VIEWTYPE_TEXT:
                view = layoutInflater.inflate(R.layout.layout_receiver_text,viewGroup,false);
                break;
            case RECEIVER_VIEWTYPE_IMAGE:
                view = layoutInflater.inflate(R.layout.layout_receiver_image,viewGroup,false);
                break;
            case RECEIVER_VIEWTYPE_IMAGE_TEXT:
                view = layoutInflater.inflate(R.layout.layout_receiver_imagetext,viewGroup,false);
                break;
            default:
                view = layoutInflater.inflate(R.layout.layout_sender_text,viewGroup,false);
                break;
        }

        return new FirestoreUserChatsViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final FirestoreUserChatsViewHolder viewHolder, final int position) {
        final BatiChatMsgModel chatMsgModel = mChatsMsgModalList.get(position);

        //======  time
        timestamp =  (Timestamp) chatMsgModel.getSentTime();
        date = timestamp.toDate() ;
        timeFormat = DateFormat.format("hh:mm a", date);

        //===== date
        dateTimestamp =  (Timestamp) mChatsMsgModalList.get(position).getSentTime();
        calenderDate = dateTimestamp.toDate() ;
        dateFormat = DateFormat.format("yyyy-MM-dd", date);

//        dateFormatView(viewHolder, position);

        //-----------------------------------------------
        dataSize = mChatsMsgModalList.size() ;
        if(dataSize>tempDataSize){
            tempDataSize = dataSize ;
            mapDateList.clear();
            mapSentList.clear();
            mapReceiveList.clear();
            for(int i=0; i<mChatsMsgModalList.size();i++){
                Timestamp timestamp1 =  (Timestamp) mChatsMsgModalList.get(i).getSentTime();
                Date date1 = timestamp1.toDate() ;
                CharSequence dateFormat1 = DateFormat.format("yyyy-MM-dd", date1);
                if(mapDateList.containsKey(dateFormat1) && mapDateList.get(dateFormat1) != null  ){
                    mapDateList.get(dateFormat1).concat( mChatsMsgModalList.get(i).getChatId());

                }else{
                    mapDateList.put(dateFormat1+"", mChatsMsgModalList.get(i).getChatId());

                }
            }
            for(int j=mChatsMsgModalList.size()-1; j>=0;j--){
                Timestamp timestamp1 =  (Timestamp) mChatsMsgModalList.get(j).getSentTime();
                Date date1 = timestamp1.toDate() ;
                CharSequence timeFormat1 = DateFormat.format("hh:mm a", date1);

                if(mChatsMsgModalList.get(j).getSentBy().equalsIgnoreCase(USER_ID)){
                    if(mapSentList.containsKey(timeFormat1) && mapSentList.get(timeFormat1) != null  ){
                        mapSentList.get(timeFormat1).concat( mChatsMsgModalList.get(j).getChatId());

                    }else{
                        mapSentList.put(timeFormat1+"", mChatsMsgModalList.get(j).getChatId());

                    }
                }else{
                    if(mapReceiveList.containsKey(timeFormat1) && mapReceiveList.get(timeFormat1) != null  ){
                        mapReceiveList.get(timeFormat1).concat( mChatsMsgModalList.get(j).getChatId());

                    }else{
                        mapReceiveList.put(timeFormat1+"", mChatsMsgModalList.get(j).getChatId());

                    }
                }


            }
        }

        bindDateTimeView(viewHolder,chatMsgModel);

        //-----------------------------------------------




        switch (getItemViewType(position)){
            case SENDER_VIEWTYPE_TEXT:
                ((FirestoreUserChatsViewHolder)viewHolder).bind1001(chatMsgModel);
                break;
            case SENDER_VIEWTYPE_IMAGE:
                ((FirestoreUserChatsViewHolder)viewHolder).bind1002(chatMsgModel);
                break;
            case SENDER_VIEWTYPE_IMAGE_TEXT:
                ((FirestoreUserChatsViewHolder)viewHolder).bind1003(chatMsgModel);
                break;
            case RECEIVER_VIEWTYPE_TEXT:
                ((FirestoreUserChatsViewHolder)viewHolder).bind2001(chatMsgModel);
                break;
            case RECEIVER_VIEWTYPE_IMAGE:
                ((FirestoreUserChatsViewHolder)viewHolder).bind2002(chatMsgModel);
                break;
            case RECEIVER_VIEWTYPE_IMAGE_TEXT:
                ((FirestoreUserChatsViewHolder)viewHolder).bind2003(chatMsgModel);
                break;
            default:
//                ((FirestoreUserChatsViewHolder)viewHolder).bind6(uploads);
                break;
        }

//        viewHolder.dateTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mCtx, "size: "+ getItemViewType(position), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void bindDateTimeView(FirestoreUserChatsViewHolder viewHolder, BatiChatMsgModel chatMsgModel) {
        if(chatMsgModel.getChatId().equalsIgnoreCase(mapSentList.get(timeFormat))){
            viewHolder.viewTime.setText( timeFormat );
            viewHolder.viewTime.setVisibility(View.VISIBLE);
        }else if(chatMsgModel.getChatId().equalsIgnoreCase(mapReceiveList.get(timeFormat))){
            viewHolder.viewTime.setText( timeFormat );
            viewHolder.viewTime.setVisibility(View.VISIBLE);
        }else{
            viewHolder.viewTime.setVisibility(View.GONE);
        }

        if(chatMsgModel.getChatId().equalsIgnoreCase(mapDateList.get(dateFormat))){
            viewHolder.dateTextView.setText(dateFormat);
            viewHolder.dateLinear.setVisibility(View.VISIBLE);
        }else{
            viewHolder.dateLinear.setVisibility(View.GONE) ;
        }
    }


    @Override
    public int getItemCount() {
        return mChatsMsgModalList.size();
    }

    public class FirestoreUserChatsViewHolder extends RecyclerView.ViewHolder {


        TextView sentText, viewTime, receiveText,  blurTextView, dateTextView;
//        ImageView sentImageView, receiveImageView;
        ImageView image1, image2, image3, image4, image5;
        ConstraintLayout image5Relative;
        LinearLayout dateLinear ;

        public FirestoreUserChatsViewHolder(View itemView) {
            super(itemView);

            viewTime =  itemView.findViewById(R.id.text_time_layout);
            sentText = (TextView)itemView.findViewById(R.id.text_sent_layout);
            receiveText =  itemView.findViewById(R.id.text_receive_layout);

            image1 =  itemView.findViewById(R.id.image1_layout);
            image2 =  itemView.findViewById(R.id.image2_layout);
            image3 =  itemView.findViewById(R.id.image3_layout);
            image4 =  itemView.findViewById(R.id.image4_layout);
            image5 =  itemView.findViewById(R.id.image5_layout);
            blurTextView =  itemView.findViewById(R.id.text_blur_layout);
            image5Relative =  itemView.findViewById(R.id.relative_image5_layout);
            dateLinear =  itemView.findViewById(R.id.linear_date_layout);
            dateTextView =  itemView.findViewById(R.id.text_date_layout);

        }
        public void bind1001(BatiChatMsgModel chatMsgModel) {

            sentText.setText( chatMsgModel.getMessage() );

//            dateFormatView();

//            if(!timeFormat.equals(tempTimeFormat)){
//                tempTimeFormat = timeFormat;
//                sentViewTime.setText( timeFormat+" "+dateFormat );
//                sentViewTime.setVisibility(View.VISIBLE);
//            }else{
//                sentViewTime.setVisibility(View.GONE);
//            }

        }




        public void bind1002(BatiChatMsgModel chatMsgModel) {
            imageShowLayout(chatMsgModel) ;

//            dateFormatView();
//            if(!timeFormat.equals(tempTimeFormat)){
//                tempTimeFormat = timeFormat;
//                sentViewTime.setText( timeFormat+" "+dateFormat );
//                sentViewTime.setVisibility(View.VISIBLE);
//            }else{
//                sentViewTime.setVisibility(View.GONE);
//            }

        }


        public void bind1003(BatiChatMsgModel chatMsgModel) {
            Log.e("text_sent", "bind1003 msg:"+chatMsgModel.getMessage()+"  image:"+ chatMsgModel.getImageThumbList().get(0) ) ;

            imageShowLayout(chatMsgModel) ;
            sentText.setText(chatMsgModel.getMessage());

//            dateFormatView();
//            if(!timeFormat.equals(tempTimeFormat)){
//                tempTimeFormat = timeFormat;
//                sentViewTime.setText( timeFormat+" "+dateFormat );
//                sentViewTime.setVisibility(View.VISIBLE);
//            }else{
//                sentViewTime.setVisibility(View.GONE);
//            }

        }

        public void bind2001(BatiChatMsgModel chatMsgModel) {
            receiveText.setText( chatMsgModel.getMessage() );

//            dateFormatView();
//            if(!timeFormat.equals(tempTimeFormat)){
//                tempTimeFormat = timeFormat;
//                receiveViewTime.setText( timeFormat+" "+dateFormat );
//                receiveViewTime.setVisibility(View.VISIBLE);
//            }else{
//                receiveViewTime.setVisibility(View.GONE);
//            }
        }

        public void bind2002(BatiChatMsgModel chatMsgModel) {
            imageShowLayout(chatMsgModel) ;

//            dateFormatView();
//            if(!timeFormat.equals(tempTimeFormat)){
//                tempTimeFormat = timeFormat;
//                receiveViewTime.setText( timeFormat+" "+dateFormat );
//                receiveViewTime.setVisibility(View.VISIBLE);
//            }else{
//                receiveViewTime.setVisibility(View.GONE);
//            }
        }

        public void bind2003(BatiChatMsgModel chatMsgModel) {
            receiveText.setText(chatMsgModel.getMessage());
            imageShowLayout(chatMsgModel) ;

//            dateFormatView();
//            if(!timeFormat.equals(tempTimeFormat)){
//                tempTimeFormat = timeFormat;
//                receiveViewTime.setText( timeFormat+" "+dateFormat );
//                receiveViewTime.setVisibility(View.VISIBLE);
//            }else{
//                receiveViewTime.setVisibility(View.GONE);
//            }
        }


        private void imageShowLayout(BatiChatMsgModel chatMsgModel) {
            if(chatMsgModel.getImageThumbList().size()==4){
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(0), R.drawable.profile_image, image1);
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(1), R.drawable.profile_image, image2);
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(2), R.drawable.profile_image, image3);
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(3), R.drawable.profile_image, image4);

                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.VISIBLE);
                image4.setVisibility(View.VISIBLE);
                image5Relative.setVisibility(View.GONE);
            } else if(chatMsgModel.getImageThumbList().size() == 3){
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(0), R.drawable.profile_image, image1);
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(1), R.drawable.profile_image, image2);
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(2), R.drawable.profile_image, image3);

                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.VISIBLE);
                image4.setVisibility(View.GONE);
                image5Relative.setVisibility(View.GONE);
            } else if(chatMsgModel.getImageThumbList().size()==2){
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(0), R.drawable.profile_image, image1);
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(1), R.drawable.profile_image, image2);

                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.GONE);
                image4.setVisibility(View.GONE);
                image5Relative.setVisibility(View.GONE);
            } else if(chatMsgModel.getImageThumbList().size() == 1){
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(0), R.drawable.profile_image, image1);

                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.GONE);
                image3.setVisibility(View.GONE);
                image4.setVisibility(View.GONE);
                image5Relative.setVisibility(View.GONE);
            } else if(chatMsgModel.getImageThumbList().size()>4){
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(0), R.drawable.profile_image, image1);
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(1), R.drawable.profile_image, image2);
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(2), R.drawable.profile_image, image3);
                AppImageLoader.loadImageInView(chatMsgModel.getImageThumbList().get(3), R.drawable.profile_image, image5);

                int subCount = chatMsgModel.getImageThumbList().size() - 3;
                blurTextView.setText("+ "+ subCount);

                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.VISIBLE);
                image4.setVisibility(View.GONE);
                image5Relative.setVisibility(View.VISIBLE);
            }


            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onIntentUrl.onIntentUrl(chatMsgModel.getImageRealList().get(0));
                }
            });
            image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onIntentUrl.onIntentUrl(chatMsgModel.getImageRealList().get(1));
                }
            });
            image3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onIntentUrl.onIntentUrl(chatMsgModel.getImageRealList().get(2));
                }
            });
            image4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onIntentUrl.onIntentUrl(chatMsgModel.getImageRealList().get(3));
                }
            });

            image5Relative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent intent = new Intent(mCtx, FirestoreChatImageActivity.class);
//                    intent.putExtra("thumbList", chatMsgModel.getImageThumbList().toString()) ;
//                    intent.putExtra("realList", chatMsgModel.getImageRealList().toString()) ;
//                    intent.putExtra("chatId", FirestoreUserChatsActivity.chatId ) ;
//                    intent.putExtra("documentId", chatMsgModel.getChatId()) ;
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    mCtx.startActivity(intent);


                    List<String> object = chatMsgModel.getImageRealList();
                    List<String> object2 = chatMsgModel.getImageThumbList();
                    Intent intent = new Intent(mCtx, FirestoreChatImageActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST",(Serializable) object  );
                    args.putSerializable("ARRAYLIST2",(Serializable) object2  );
                    intent.putExtra("BUNDLE",args);
                    intent.putExtra("thumbList", chatMsgModel.getImageThumbList().toString()) ;
                    intent.putExtra("realList", chatMsgModel.getImageRealList().toString()) ;
                    intent.putExtra("chatId", FirestoreUserChatsActivity.chatId ) ;
                    intent.putExtra("documentId", chatMsgModel.getChatId()) ;
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mCtx.startActivity(intent);



                }
            });
        }

    }



    //   next time need
    private void dateFormatView(FirestoreUserChatsViewHolder viewHolder, int position) {
        Log.e("position", "position:"+ position+"   tempPosition:"+tempPosition ) ;

        if(position>tempPosition){
            tempPosition=position ;
            if(!dateFormat.equals(tempDateFormat)){
                tempDateFormat = dateFormat;

                viewHolder.dateTextView.setText( dateFormat );
                viewHolder.dateLinear.setVisibility(View.VISIBLE);
            }else{
                viewHolder.dateLinear.setVisibility(View.GONE);
            }
        }else{
            tempPosition=position ;
        }
    }
}