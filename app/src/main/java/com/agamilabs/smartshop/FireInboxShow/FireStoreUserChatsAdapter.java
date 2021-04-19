package com.agamilabs.smartshop.FireInboxShow;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.google.firebase.firestore.FieldValue;

import com.google.firebase.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FireStoreUserChatsAdapter extends RecyclerView.Adapter<FireStoreUserChatsAdapter.PostViewHolder> {


    private Context mCtx;
    private List<BatiChatMsgModel> mChatsMsgModalList;

    public FireStoreUserChatsAdapter(Context mCtx, List<BatiChatMsgModel> mChatsMsgModal) {
        this.mCtx = mCtx;
        this.mChatsMsgModalList = mChatsMsgModal;
    }

    @Override
    public FireStoreUserChatsAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_firestore_msg_chats, null);
        return new FireStoreUserChatsAdapter.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final FireStoreUserChatsAdapter.PostViewHolder holder, final int position) {
        Collections.sort(mChatsMsgModalList, new Comparator<BatiChatMsgModel>() {
            @Override
            public int compare(BatiChatMsgModel lhs, BatiChatMsgModel rhs) {
                return lhs.getSentTime().toString().compareTo(rhs.getSentTime().toString());
            }
        });
        final BatiChatMsgModel chatMsgModel = mChatsMsgModalList.get(position);

//        ((PostViewHolder) holder).bind(chatMsgModel) ;
        ((PostViewHolder) holder).bind1(mChatsMsgModalList, position) ;



    }

    @Override
    public int getItemCount() {
        return mChatsMsgModalList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

      RelativeLayout mSentRelative, mReceiveRelative ;
      TextView mSentMsgTV, mSentTimeTV, mReceiveMsgTV, mReceiveTimeTV ;



        public PostViewHolder(View itemView) {
            super(itemView);

            mSentRelative = itemView.findViewById(R.id.relative_sent);
            mReceiveRelative = itemView.findViewById(R.id.relative_receive);
            mSentMsgTV = itemView.findViewById(R.id.text_sent);
            mSentTimeTV = itemView.findViewById(R.id.text_sent_time);
            mReceiveMsgTV = itemView.findViewById(R.id.text_receive);
            mReceiveTimeTV = itemView.findViewById(R.id.text_receive_time);

        }

        public void bind(final BatiChatMsgModel chatMsgModel){
//            Timestamp timestamp
            Timestamp timestamp =  (Timestamp) chatMsgModel.getSentTime();
            Date date = timestamp.toDate() ;
            CharSequence dateFormat = DateFormat.format("yyyy-MM-dd", date);
            CharSequence timeFormat = DateFormat.format("hh:mm a", date);


            if(FireStoreUserActivity.USER_ID.equalsIgnoreCase(chatMsgModel.getSentBy())){
                mSentRelative.setVisibility(View.VISIBLE);
                mSentMsgTV.setText(chatMsgModel.getMessage());
                mSentTimeTV.setText(dateFormat+"\n"+timeFormat );

            }else{
                mReceiveRelative.setVisibility(View.VISIBLE);
                mReceiveMsgTV.setText(chatMsgModel.getMessage());
                mReceiveTimeTV.setText(dateFormat+"\n"+timeFormat);
            }

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mCtx, FirestoreUserChatsActivity.class) ;
//                    intent.putExtra("chatID", userChats.getDocumentId()) ;
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    mCtx.startActivity(intent);
//                }
//            });

        }

        public void bind1(List<BatiChatMsgModel> mChatsMsgModalList, int position) {
//            AppController.getAppController().getInAppNotifier().log("position", "mChatsMsgModalList: "+mChatsMsgModalList.size() );

            Timestamp timestamp, timestamp2 ;
            Date date, date2 ;
            CharSequence timeFormat, dateTimeFormat1, dateTimeFormat2 = null;
            timestamp =  (Timestamp) mChatsMsgModalList.get(position).getSentTime();
            date = timestamp.toDate() ;

            timeFormat = DateFormat.format("hh:mm a", date);
//            CharSequence dateFormat = DateFormat.format("yyyy-MM-dd", date);
            dateTimeFormat1 = DateFormat.format("yyyy-MM-dd hh:mm a", date);





            if(FireStoreUserActivity.USER_ID.equalsIgnoreCase( mChatsMsgModalList.get(position).getSentBy())){
                mSentRelative.setVisibility(View.VISIBLE);
                mSentMsgTV.setText( mChatsMsgModalList.get(position).getMessage());
                mSentTimeTV.setText(timeFormat );


            }else{
                mReceiveRelative.setVisibility(View.VISIBLE);
                mReceiveMsgTV.setText( mChatsMsgModalList.get(position).getMessage());
                mReceiveTimeTV.setText(timeFormat);
            }
        }
    }
}