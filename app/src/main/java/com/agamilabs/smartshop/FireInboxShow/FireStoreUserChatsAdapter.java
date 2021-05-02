package com.agamilabs.smartshop.FireInboxShow;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;

import com.google.firebase.Timestamp;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FireStoreUserChatsAdapter extends RecyclerView.Adapter<FireStoreUserChatsAdapter.FirestoreUserChatsViewHolder> {

    private Context mCtx;
    private List<BatiChatMsgModel> mChatsMsgModalList;
    int mPageNumber = 1 ;
    CharSequence tempSentDate =null, tempReceiveDate=null ;
    CharSequence tempSentTime =null, tempReceiveTime=null ;

    HashMap<String, String> mapDateList = new HashMap<>() ;
    HashMap<String, String> mapSentList = new HashMap<>() ;
    HashMap<String, String> mapReceiveList = new HashMap<>() ;

    int dataSize=0, tempDataSize=0 ;
    SharedPreferences sharedPreferences ;
    static String SHARED_PREFS = "admin_store";
    String USER_ID ;


    public FireStoreUserChatsAdapter(Context mCtx, List<BatiChatMsgModel> mChatsMsgModal) {
        this.mCtx = mCtx;
        this.mChatsMsgModalList = mChatsMsgModal;

    }

    @Override
    public FirestoreUserChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_firestore_msg_chats, null);
        FirestoreUserChatsViewHolder holder = new FirestoreUserChatsViewHolder(view);

        sharedPreferences = mCtx.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        USER_ID = sharedPreferences.getString("admin_user_id", "");

        holder.setIsRecyclable(false);
        return new FirestoreUserChatsViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final FirestoreUserChatsViewHolder holder, final int position) {

//        holder.setIsRecyclable(false);
        Collections.sort(mChatsMsgModalList, new Comparator<BatiChatMsgModel>() {
            @Override
            public int compare(BatiChatMsgModel lhs, BatiChatMsgModel rhs) {
                return lhs.getSentTime().toString().compareTo(rhs.getSentTime().toString());
            }
        });

        final BatiChatMsgModel chatMsgModel = mChatsMsgModalList.get(position);
//        Timestamp timestamp =  (Timestamp) chatMsgModel.getSentTime();
//        Date date = timestamp.toDate() ;
//        CharSequence dateFormat = DateFormat.format("yyyy-MM-dd", date);
//        CharSequence timeFormat = DateFormat.format("hh:mm a", date);

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




//        ((FirestoreUserChatsViewHolder) holder).bind(chatMsgModel) ;

        ((FirestoreUserChatsViewHolder) holder).bind2(chatMsgModel) ;

//        ((FirestoreUserChatsViewHolder) holder).bind1(mChatsMsgModalList, position) ;



    }

    @Override
    public int getItemCount() {
        return mChatsMsgModalList.size();
    }

    class FirestoreUserChatsViewHolder extends RecyclerView.ViewHolder {

      RelativeLayout mSentRelative, mReceiveRelative ;
      TextView mSentMsgTV, mSentTimeTV, mReceiveMsgTV, mReceiveTimeTV, mDateShowTV ;
      LinearLayout mDateLinear;



        public FirestoreUserChatsViewHolder(View itemView) {
            super(itemView);

            mSentRelative = itemView.findViewById(R.id.relative_sent);
            mReceiveRelative = itemView.findViewById(R.id.relative_receive);
            mSentMsgTV = itemView.findViewById(R.id.text_sent);
            mSentTimeTV = itemView.findViewById(R.id.text_sent_time);
            mReceiveMsgTV = itemView.findViewById(R.id.text_receive);
            mReceiveTimeTV = itemView.findViewById(R.id.text_receive_time);
            mDateShowTV = itemView.findViewById(R.id.text_date_layout);
            mDateLinear = itemView.findViewById(R.id.linear_layout);

        }

        public void bind2(final BatiChatMsgModel chatMsgModel){


            Timestamp timestamp =  (Timestamp) chatMsgModel.getSentTime();
            Date date = timestamp.toDate() ;
//            Log.e("map_date",  " date: "+ DateFormat.format("d MMM, yyyy", date)  ) ;
            CharSequence dateFormatText = DateFormat.format("d MMM, yyyy", date);
            CharSequence dateFormat = DateFormat.format("yyyy-MM-dd", date);
            CharSequence timeFormat = DateFormat.format("hh:mm a", date);


            if(mapDateList.get(dateFormat)!=null && mapDateList.get(dateFormat).equalsIgnoreCase(chatMsgModel.getChatId())){
                mDateLinear.setVisibility(View.VISIBLE);
                mDateShowTV.setText(dateFormatText);
                Log.e("map", " myList cKey: "+ mapDateList.get(dateFormat) +  " ID:   "+ chatMsgModel.getChatId()) ;
            }else{
                mDateLinear.setVisibility(View.GONE);
            }



            if(USER_ID.equalsIgnoreCase(chatMsgModel.getSentBy())){

                mSentRelative.setVisibility(View.VISIBLE);
                mReceiveRelative.setVisibility(View.GONE);
                mSentMsgTV.setText(chatMsgModel.getMessage());
//                mSentTimeTV.setText( timeFormat+"\n"+dateFormat );

//                mDateLinear.setVisibility(View.VISIBLE);
//                mDateShowTV.setText(dateFormat);
//                if(tempSentDate!=null && tempSentDate.equals(dateFormat) ){
//                    mDateLinear.setVisibility(View.GONE);
////                    Log.e("dateFormat", tempReceiveDate+"  dateformat 2: "+ dateFormat);
//
//                }
//                if(tempSentDate==null || !tempSentDate.equals(dateFormat)){
//
//                    tempSentDate = dateFormat;
////                    Log.e("dateFormat", tempReceiveDate+"  dateformat 1: "+ dateFormat);
//                }

//                if(mapDateList.get(dateFormat)!=null && mapDateList.get(dateFormat).equalsIgnoreCase(chatMsgModel.getChatId())){
//                    mDateLinear.setVisibility(View.VISIBLE);
//                    mDateShowTV.setText(dateFormat);
//                    Log.e("map", " myList cKey: "+ mapDateList.get(dateFormat) +  " ID:   "+ chatMsgModel.getChatId()) ;
//                }else{
//                    mDateLinear.setVisibility(View.GONE);
//                }

                if(mapSentList.get(timeFormat)!=null && mapSentList.get(timeFormat).equalsIgnoreCase(chatMsgModel.getChatId())){
                    mSentTimeTV.setVisibility(View.VISIBLE);
                    mSentTimeTV.setText(timeFormat);
                    Log.e("map", " myList cKey: "+ mapSentList.get(timeFormat) +  " ID:   "+ chatMsgModel.getChatId()) ;
                }else{
                    mSentTimeTV.setVisibility(View.GONE);
                }





            }else{
                mSentRelative.setVisibility(View.GONE);
                mReceiveRelative.setVisibility(View.VISIBLE);
                mReceiveMsgTV.setText(chatMsgModel.getMessage());
//                mReceiveTimeTV.setText(timeFormat+"\n"+dateFormat);

//                mDateLinear.setVisibility(View.VISIBLE);
//                mDateShowTV.setText(dateFormat);

//                Log.e("dateFormat", tempReceiveDate+"  dateformat: "+ dateFormat);

                Log.e("dateFormat", tempReceiveDate+"  dateformat 0: "+ dateFormat);

//                if(mapDateList.get(dateFormat)!=null && mapDateList.get(dateFormat).equalsIgnoreCase(chatMsgModel.getChatId())){
//                    mDateLinear.setVisibility(View.VISIBLE);
//                    mDateShowTV.setText(dateFormat);
//                }else{
//                    mDateLinear.setVisibility(View.GONE);
//                }

                Log.e("map_key", " myList cKey: "+ mapDateList.get(timeFormat) +  " ID:   "+ chatMsgModel.getChatId()) ;
                Log.e("map_key", " myList cKey: "+ mapReceiveList.keySet() ) ;
                if(mapReceiveList.get(timeFormat)!=null && mapReceiveList.get(timeFormat).equalsIgnoreCase(chatMsgModel.getChatId())){
                    mReceiveTimeTV.setVisibility(View.VISIBLE);
                    mReceiveTimeTV.setText(timeFormat);

                }else{
                    mReceiveTimeTV.setVisibility(View.GONE);
                }





//                if(tempReceiveDate!=null && tempReceiveDate.equals(dateFormat) ){
//                    mDateLinear.setVisibility(View.GONE);
//                    Log.e("dateFormat", tempReceiveDate+"  dateformat 2: "+ dateFormat);
//
//                }
//                if(tempReceiveDate==null || !tempReceiveDate.equals(dateFormat)){
//
//                    tempReceiveDate = dateFormat;
//                    Log.e("dateFormat", tempReceiveDate+"  dateformat 1: "+ dateFormat);
//                }



//                if(tempReceiveTime != null && tempReceiveTime.equals(timeFormat)){
//
//                }
            }





        }





























        public void bind(final BatiChatMsgModel chatMsgModel){
//            Timestamp timestamp
            Timestamp timestamp_1 =  (Timestamp) chatMsgModel.getSentTime();
            Date date_1 = timestamp_1.toDate() ;
            CharSequence dateFormat_1 = DateFormat.format("yyyy-MM-dd", date_1);
            CharSequence timeFormat_1 = DateFormat.format("hh:mm a", date_1);


            if(USER_ID.equalsIgnoreCase(chatMsgModel.getSentBy())){
                mSentRelative.setVisibility(View.VISIBLE);
                mReceiveRelative.setVisibility(View.GONE);
                mSentMsgTV.setText(chatMsgModel.getMessage());
                mSentTimeTV.setText(dateFormat_1+"\n"+timeFormat_1 );

            }else{
                mReceiveRelative.setVisibility(View.VISIBLE);
                mSentRelative.setVisibility(View.GONE);
                mReceiveMsgTV.setText(chatMsgModel.getMessage());
                mReceiveTimeTV.setText(dateFormat_1+"\n"+timeFormat_1);
            }
        }

        public void bind1(List<BatiChatMsgModel> mChatsMsgModalList, int position) {

            Log.e("posi_bind1", "position: "+ position );
            Timestamp timestamp, timestampNext, timestampPrev ;
            Date date, dateNext = null, datePrev ;
            CharSequence timeFormat, dateTimeFormat1, dateTimeFormat2 = null, dateFormat1, dateFormatNext = null, dateFormatPrev = null;
            timestamp =  (Timestamp) mChatsMsgModalList.get(position).getSentTime();
            date = timestamp.toDate() ;

            timeFormat = DateFormat.format("hh:mm a", date);
//            CharSequence dateFormat = DateFormat.format("yyyy-MM-dd", date);
            dateTimeFormat1 = DateFormat.format("yyyy-MM-dd hh:mm a", date);
            dateFormat1 = DateFormat.format("yyyy-MM-dd", date);

            if(mChatsMsgModalList.size()>=(position+1)){
                if(position!=0){
                    timestampPrev =  (Timestamp) mChatsMsgModalList.get(position-1).getSentTime();
                    datePrev = timestampPrev.toDate() ;
                    dateFormatPrev = DateFormat.format("yyyy-MM-dd", datePrev);
                }
                if(mChatsMsgModalList.size()>(position+1)){

                    timestampNext =  (Timestamp) mChatsMsgModalList.get(position+1).getSentTime();
                    dateNext = timestampNext.toDate() ;
                    dateFormatNext = DateFormat.format("yyyy-MM-dd", dateNext);
                    if( mChatsMsgModalList.get(position).getSentBy().equalsIgnoreCase( mChatsMsgModalList.get(position+1).getSentBy())){
                        dateTimeFormat2 = DateFormat.format("yyyy-MM-dd hh:mm a", dateNext);
                    }
                }


            }


            boolean b = dateTimeFormat2 != null && dateTimeFormat2.toString().equalsIgnoreCase(dateTimeFormat1.toString());

            AppController.getAppController().getInAppNotifier().log("date", dateFormat1+" \n dateFormatNext: "+dateFormatNext
                    +" \ndateFormatPrev: "+dateFormatPrev+" \n position: "+position );
            if((dateFormat1.equals(dateFormatNext) && !dateFormat1.equals(dateFormatPrev)) || dateFormatPrev==null){
                mDateShowTV.setText(dateFormat1);
                mDateLinear.setVisibility(View.VISIBLE);
            }

            if(USER_ID.equalsIgnoreCase( mChatsMsgModalList.get(position).getSentBy())){
                mSentRelative.setVisibility(View.VISIBLE);
                mSentMsgTV.setText( mChatsMsgModalList.get(position).getMessage());
                mSentTimeTV.setText(timeFormat);

                if(b){
                    mSentTimeTV.setVisibility(View.GONE);
                }

            }else{
                mReceiveRelative.setVisibility(View.VISIBLE);
                mReceiveMsgTV.setText( mChatsMsgModalList.get(position).getMessage());
                mReceiveTimeTV.setText(timeFormat);
                if(b){
                    mReceiveTimeTV.setVisibility(View.GONE);
                }
            }
        }
    }
}