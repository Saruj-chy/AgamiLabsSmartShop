package com.agamilabs.smartshop.FireInboxShow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppImageLoader;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FireStoreUserAdapter extends RecyclerView.Adapter<FireStoreUserAdapter.PostViewHolder> {


    private Context mCtx;
    private List<BatiUsersDetailsModal> mUserDetailsModalList;
    private List<BatiChatMsgModel> mChatsMsgList;
    private List<BatiUserChatsModal> mBatiUserChatsList;


    public FireStoreUserAdapter(Context mCtx,  List<BatiUsersDetailsModal> mUserDetailsModalList, List<BatiChatMsgModel> mChatsMsgList, List<BatiUserChatsModal> mBatiUserChatsList ) {
        this.mCtx = mCtx;
        this.mUserDetailsModalList = mUserDetailsModalList;
        this.mChatsMsgList = mChatsMsgList;
        this.mBatiUserChatsList = mBatiUserChatsList;

    }
    //  subject search option
    public void filterList(List<BatiUsersDetailsModal> filteredList) {
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



        ((PostViewHolder) holder).bind(batiUserChatsModal) ;



    }

    @Override
    public int getItemCount() {
        return mUserDetailsModalList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUserName, textViewUserStatus ;
        CircleImageView mUserImageLogo ;



        public PostViewHolder(View itemView) {
            super(itemView);

            mUserImageLogo = itemView.findViewById(R.id.image_user);
            textViewUserName = itemView.findViewById(R.id.text_user_name);
            textViewUserStatus = itemView.findViewById(R.id.text_user_status);

        }



        public void bind(BatiUserChatsModal batiUserChatsModal ) {

           for(int i=0; i<mBatiUserChatsList.size(); i++){
               if(batiUserChatsModal.getDocumentId().equalsIgnoreCase(mUserDetailsModalList.get(i).getDocumentId())){
                   AppImageLoader.loadImageInView(mUserDetailsModalList.get(i).getPhoto(), R.drawable.profile_image, (ImageView)mUserImageLogo);

                   textViewUserName.setText(mUserDetailsModalList.get(i).getName());
                   textViewUserStatus.setText(mChatsMsgList.get(i).getMessage());
                   if(batiUserChatsModal.getUnseen_message()==0){
                       textViewUserStatus.setTypeface(null, Typeface.NORMAL);
                   }

                   int tempI = i;
                   itemView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Intent intent = new Intent(mCtx, FirestoreUserChatsActivity.class) ;
                           intent.putExtra("chatID", mUserDetailsModalList.get(tempI).getDocumentId() ) ;
                           intent.putExtra("chat_name", mUserDetailsModalList.get(tempI).getName() ) ;
                           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           mCtx.startActivity(intent);
//                    Toast.makeText(mCtx, "chatID: "+ userDetails.getDocumentId(), Toast.LENGTH_SHORT).show();
                       }
                   });
               }
           }



        }






























    }
}