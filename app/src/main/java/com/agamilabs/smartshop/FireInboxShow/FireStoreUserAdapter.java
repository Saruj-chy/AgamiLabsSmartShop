package com.agamilabs.smartshop.FireInboxShow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.FireInboxShow.activity.FirestoreUserChatsActivity;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FireStoreUserAdapter extends RecyclerView.Adapter<FireStoreUserAdapter.PostViewHolder> {
    private Context mCtx;
    private List<BatiUsersDetailsModal> mUserDetailsModalList;
    private List<BatiChatMsgModel> mChatsMsgList;
    private List<BatiUserChatsModal> mBatiUserChatsList;

    private boolean filterCheck = false ;

    public FireStoreUserAdapter(Context mCtx,  List<BatiUsersDetailsModal> mUserDetailsModalList, List<BatiChatMsgModel> mChatsMsgList, List<BatiUserChatsModal> mBatiUserChatsList ) {
        this.mCtx = mCtx;
        this.mUserDetailsModalList = mUserDetailsModalList;
        this.mChatsMsgList = mChatsMsgList;
        this.mBatiUserChatsList = mBatiUserChatsList;

    }
    //  subject search option
    public void filterList(List<BatiUsersDetailsModal> filteredList, boolean textExist) {
        filterCheck = textExist ;
        mUserDetailsModalList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public FireStoreUserAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_firestore_inbox_user, null);
        return new FireStoreUserAdapter.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final FireStoreUserAdapter.PostViewHolder holder, final int position) {
        final BatiUsersDetailsModal batiUsersDetailsModal = mUserDetailsModalList.get(position);
        final BatiChatMsgModel batiChatMsgModel = mChatsMsgList.get(position);
        final BatiUserChatsModal batiUserChatsModal = mBatiUserChatsList.get(position);



        ((PostViewHolder) holder).bind(batiUserChatsModal, batiUsersDetailsModal, batiChatMsgModel) ;



    }

    @Override
    public int getItemCount() {
        return mUserDetailsModalList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUserName, textViewUserStatus, textViewUserLastTime ;
        CircleImageView mUserImageLogo ;
        ImageButton mActiveImgbtn;

        public PostViewHolder(View itemView) {
            super(itemView);

            mUserImageLogo = itemView.findViewById(R.id.image_user);
            textViewUserName = itemView.findViewById(R.id.text_user_name);
            textViewUserStatus = itemView.findViewById(R.id.text_user_status);
            textViewUserLastTime = itemView.findViewById(R.id.text_user_last_time);
            mActiveImgbtn = itemView.findViewById(R.id.imgbtn_active);

        }

        public void bind(BatiUserChatsModal batiUserChatsModal, BatiUsersDetailsModal batiUsersDetailsModal, BatiChatMsgModel batiChatMsgModel ) {

           for(int i=0; i<mBatiUserChatsList.size(); i++){

               if(!filterCheck){
                   if(batiUserChatsModal.getDocumentId().equalsIgnoreCase(mUserDetailsModalList.get(i).getDocumentId())  ){
                       AppImageLoader.loadImageInView(mUserDetailsModalList.get(i).getPhoto()+"", R.drawable.profile_image, (ImageView)mUserImageLogo);

                       textViewUserName.setText(mUserDetailsModalList.get(i).getName()+"");

                       if(!batiUserChatsModal.getDocumentId().equalsIgnoreCase(mChatsMsgList.get(i).getDocumentId())){
                           for(int j=0; j<mChatsMsgList.size(); j++){
                               if(batiUserChatsModal.getDocumentId().equalsIgnoreCase(mChatsMsgList.get(j).getDocumentId())){
                                   if(!mChatsMsgList.get(i).getMessage().equalsIgnoreCase("")){
                                       textViewUserStatus.setText(mChatsMsgList.get(i).getMessage());
                                   }else{
                                       textViewUserStatus.setText("sent an attachment");
                                   }


                                   Timestamp timestamp =  (Timestamp) mChatsMsgList.get(j).getSentTime();
                                   Date date = timestamp.toDate() ;
                                   CharSequence timeFormat = DateFormat.format("hh:mm a", date);
                                   textViewUserLastTime.setText(timeFormat);
                               }
                           }
                       }else{
                           if(!mChatsMsgList.get(i).getMessage().equalsIgnoreCase("")){
                               textViewUserStatus.setText(mChatsMsgList.get(i).getMessage());
                           }else{
                               textViewUserStatus.setText("sent an attachment");
                           }


                           Timestamp timestamp =  (Timestamp) mChatsMsgList.get(i).getSentTime();
                           Date date = timestamp.toDate() ;
                           CharSequence timeFormat = DateFormat.format("hh:mm a", date);
                           textViewUserLastTime.setText(timeFormat);
                       }

                       //change
                       if(mBatiUserChatsList.get(i).getUnseen_message()==0){
                           textViewUserStatus.setTypeface(null, Typeface.NORMAL);
                           mActiveImgbtn.setVisibility(View.GONE);
                       }else{
                           Log.e("unseen_msg", " unseen msg out: "+ batiUserChatsModal.getUnseen_message()) ;
                           textViewUserStatus.setTypeface(null, Typeface.BOLD);
                           textViewUserStatus.setTypeface(null, Typeface.BOLD_ITALIC);
                           mActiveImgbtn.setVisibility(View.VISIBLE);
                       }


                       int tempI = i;
                       itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Intent intent = new Intent(mCtx, FirestoreUserChatsActivity.class) ;
                               intent.putExtra("chatID", mUserDetailsModalList.get(tempI).getDocumentId() ) ;
                               intent.putExtra("chat_name", mUserDetailsModalList.get(tempI).getName()+"" ) ;
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               mCtx.startActivity(intent);
                           }
                       });
                   }
               }else{
                   if(batiUsersDetailsModal.getDocumentId().equalsIgnoreCase(mBatiUserChatsList.get(i).getDocumentId())  ){
                       AppImageLoader.loadImageInView(batiUsersDetailsModal.getPhoto()+"", R.drawable.profile_image, (ImageView)mUserImageLogo);

                       textViewUserName.setText(batiUsersDetailsModal.getName()+"");

                       for (int j=0; j<mChatsMsgList.size(); j++ ){
                           if (batiUsersDetailsModal.getDocumentId().equalsIgnoreCase(mChatsMsgList.get(j).getDocumentId())){
                               textViewUserStatus.setText( mChatsMsgList.get(j).getMessage() );

                               Timestamp timestamp =  (Timestamp) mChatsMsgList.get(j).getSentTime();
                               Date date = timestamp.toDate() ;
                               CharSequence timeFormat = DateFormat.format("hh:mm a", date);
                               textViewUserLastTime.setText(timeFormat);
                           }
                       }

                       //change           mBatiUserChatsList.get(i).getUnseen_message()==0

                       if(mBatiUserChatsList.get(i).getUnseen_message()==0){
                           textViewUserStatus.setTypeface(null, Typeface.NORMAL);
                       }
                       itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Intent intent = new Intent(mCtx, FirestoreUserChatsActivity.class) ;
                               intent.putExtra("chatID", batiUsersDetailsModal.getDocumentId() ) ;
                               intent.putExtra("chat_name", batiUsersDetailsModal.getName()+"" ) ;
                               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               mCtx.startActivity(intent);
                           }
                       });

                   }
               }
           }

        }
    }
}