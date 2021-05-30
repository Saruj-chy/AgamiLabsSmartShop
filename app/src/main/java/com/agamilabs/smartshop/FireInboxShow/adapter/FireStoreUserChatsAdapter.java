package com.agamilabs.smartshop.FireInboxShow.adapter;

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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.FireInboxShow.BatiChatMsgModel;
import com.agamilabs.smartshop.FireInboxShow.Interface.OnIntentUrl;
import com.agamilabs.smartshop.R;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
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

    OnIntentUrl onIntentUrl;

    List<String> imageList = new ArrayList<>() ;


    public FireStoreUserChatsAdapter(Context mCtx, List<BatiChatMsgModel> mChatsMsgModal, OnIntentUrl onIntentUrl) {
        this.mCtx = mCtx;
        this.mChatsMsgModalList = mChatsMsgModal;
        this.onIntentUrl = onIntentUrl;

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
        Collections.sort(mChatsMsgModalList, new Comparator<BatiChatMsgModel>() {
            @Override
            public int compare(BatiChatMsgModel lhs, BatiChatMsgModel rhs) {
                return lhs.getSentTime().toString().compareTo(rhs.getSentTime().toString());
            }
        });

        final BatiChatMsgModel chatMsgModel = mChatsMsgModalList.get(position);

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


        ((FirestoreUserChatsViewHolder) holder).bind(chatMsgModel) ;

    }

    @Override
    public int getItemCount() {
        return mChatsMsgModalList.size();
    }

    class FirestoreUserChatsViewHolder extends RecyclerView.ViewHolder {


        RelativeLayout mSentRelative, mReceiveRelative ;
        RecyclerView mSentRV, mReceiveRV ;
        TextView mSentMsgTV, mSentTimeTV, mReceiveMsgTV, mReceiveTimeTV, mDateShowTV ;
        LinearLayout mDateLinear;
        private NestedFirestoreUserChatsAdapter mNestedFirestoreUserChatsAdapter;
        List<String> mNestedThumbImageList = new ArrayList<>() ;
        List<String> mNestedRealImageList = new ArrayList<>() ;



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

            mSentRV = itemView.findViewById(R.id.sent_rv);
            mReceiveRV = itemView.findViewById(R.id.receive_rv);
            mNestedFirestoreUserChatsAdapter = new NestedFirestoreUserChatsAdapter(mCtx, mNestedThumbImageList, mNestedRealImageList, onIntentUrl);
            GridLayoutManager manager1 = new GridLayoutManager(mCtx, 3, GridLayoutManager.VERTICAL, false);
            GridLayoutManager manager2 = new GridLayoutManager(mCtx, 3, GridLayoutManager.VERTICAL, false);
            mSentRV.setLayoutManager(manager1);  // set horizontal LM
            mSentRV.setAdapter(mNestedFirestoreUserChatsAdapter);

            mReceiveRV.setLayoutManager(manager2);  // set horizontal LM
            mReceiveRV.setAdapter(mNestedFirestoreUserChatsAdapter);

        }

        public void bind(final BatiChatMsgModel chatMsgModel){


            Timestamp timestamp =  (Timestamp) chatMsgModel.getSentTime();
            Date date = timestamp.toDate() ;
            CharSequence dateFormatText = DateFormat.format("d MMM, yyyy", date);
            CharSequence dateFormat = DateFormat.format("yyyy-MM-dd", date);
            CharSequence timeFormat = DateFormat.format("hh:mm a", date);


            if(mapDateList.get(dateFormat)!=null && mapDateList.get(dateFormat).equalsIgnoreCase(chatMsgModel.getChatId())){
                mDateLinear.setVisibility(View.VISIBLE);
                mDateShowTV.setText(dateFormatText);
            }else{
                mDateLinear.setVisibility(View.GONE);
            }

            List<String> imageThumbList  = new ArrayList<>() ;
            List<String> imageRealList  = new ArrayList<>() ;
            imageThumbList = chatMsgModel.getImageThumbList() ;
            imageRealList = chatMsgModel.getImageRealList() ;
            Log.e("image_Size", "\n msg met ========: "+ "   msg:   "+ chatMsgModel.getMessage()+"   ID:   "+chatMsgModel.getChatId() ) ;

            if(USER_ID.equalsIgnoreCase(chatMsgModel.getSentBy())){

                mSentRelative.setVisibility(View.VISIBLE);
                mReceiveRelative.setVisibility(View.GONE);
                if(!chatMsgModel.getMessage().isEmpty()){
                    mSentMsgTV.setText(chatMsgModel.getMessage());
                    mSentMsgTV.setVisibility(View.VISIBLE);
                }else{
                    mSentMsgTV.setVisibility(View.GONE);
                }

                if(mapSentList.get(timeFormat)!=null && mapSentList.get(timeFormat).equalsIgnoreCase(chatMsgModel.getChatId())){
                    mSentTimeTV.setVisibility(View.VISIBLE);
                    mSentTimeTV.setText(timeFormat);


                }else{
                    mSentTimeTV.setVisibility(View.GONE);
                }
                ImageViewAdapter(chatMsgModel, imageThumbList, imageRealList);

            }else{
                mSentRelative.setVisibility(View.GONE);
                mReceiveRelative.setVisibility(View.VISIBLE);
                if(!chatMsgModel.getMessage().isEmpty()){
                    mReceiveMsgTV.setText(chatMsgModel.getMessage());
                    mReceiveMsgTV.setVisibility(View.VISIBLE);
                }else{
                    mReceiveMsgTV.setVisibility(View.GONE);
                }
//                if(chatMsgModel.getImageList().size()==0){
//                    mReceiveRV.setVisibility(View.GONE);
//                }

                if(mapReceiveList.get(timeFormat)!=null && mapReceiveList.get(timeFormat).equalsIgnoreCase(chatMsgModel.getChatId())){
                    mReceiveTimeTV.setVisibility(View.VISIBLE);
                    mReceiveTimeTV.setText(timeFormat);


                }else{
                    mReceiveTimeTV.setVisibility(View.GONE);
                }
                ImageViewAdapter(chatMsgModel, imageThumbList, imageRealList);
            }

        }

        private void ImageViewAdapter(BatiChatMsgModel chatMsgModel, List<String> imageThumbList, List<String> imageRealList) {

            if(imageThumbList!=null){
                mNestedThumbImageList.clear();
                mNestedRealImageList.clear();
                for(int i=0; i<imageThumbList.size(); i++){
                    this.mNestedThumbImageList.add( this.mNestedThumbImageList.size(), imageThumbList.get(i) );
                    this.mNestedRealImageList.add( this.mNestedRealImageList.size(), imageRealList.get(i) );
                }

                mNestedFirestoreUserChatsAdapter.notifyDataSetChanged();

                if(chatMsgModel.getImageThumbList().size()==0){
                    mSentRV.setVisibility(View.GONE);
                    mReceiveRV.setVisibility(View.GONE);
                }else{
                    mSentRV.setVisibility(View.VISIBLE);
                    mReceiveRV.setVisibility(View.VISIBLE);
                }
            }
            else if(imageThumbList==null){

                mSentRV.setVisibility(View.GONE);
                mReceiveRV.setVisibility(View.GONE);
            }
        }

    }
}