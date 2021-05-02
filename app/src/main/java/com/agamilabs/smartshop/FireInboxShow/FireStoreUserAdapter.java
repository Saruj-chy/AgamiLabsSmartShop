package com.agamilabs.smartshop.FireInboxShow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.activity.StockReportActivity;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.google.type.Date;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FireStoreUserAdapter extends RecyclerView.Adapter<FireStoreUserAdapter.PostViewHolder> {


    private Context mCtx;
    private List<BatiUsersDetailsModal> mUserDetailsModalList;
    private List<BatiChatMsgModel> mChatsMsgList;
    private List<BatiUserChatsModal> mBatiUserChatsList;

    int dataSize=0, tempDataSize=0 ;
    HashMap<String, String> mapArrayList = new HashMap<>() ;


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
//        Log.e("chat_list", "details list:  "+ mUserDetailsModalList.size() ) ;
//        Log.e("chat_list", "list:  "+ mChatsMsgList.size() ) ;

//        dataSize = mUserDetailsModalList.size() ;
//        if(dataSize>tempDataSize) {
//            tempDataSize = dataSize;
//
//
//            for (int i=0; i<mBatiUserChatsList.size() ; i++){
//                Log.e("doc_id", "userChats id:  "+ mBatiUserChatsList.get(i).getDocumentId() ) ;
//                for (int j=0; j<mUserDetailsModalList.size(); j++){
//                    if(mUserDetailsModalList.get(j).getDocumentId().equalsIgnoreCase(mBatiUserChatsList.get(i).getDocumentId() ) ){
//                        Log.e("doc_id", "details id:  "+ mUserDetailsModalList.get(j).getDocumentId() ) ;
//                        Log.e("doc_id", "details id:  "+ mUserDetailsModalList.get(j).getName() ) ;
//
//                        if(mapDateList.containsKey(dateFormat1) && mapDateList.get(dateFormat1) != null  ){
//                            mapDateList.get(dateFormat1).concat( mChatsMsgModalList.get(i).getChatId());
//
//                        }else{
//                            mapArrayList.put(mBatiUserChatsList.get(i).getDocumentId(), mChatsMsgModalList.get(i).getChatId());
//
//                        }
//                    }
//                }
//            }
//
//
//
//
//
//
//
//        }
//        ((PostViewHolder) holder).superBind(mUserDetailsModalList, mChatsMsgList, mBatiUserChatsList) ;
        ((PostViewHolder) holder).multiBind(batiUserChatsModal, batiUsersDetailsModal) ;







        //====================    bind
//        ((PostViewHolder) holder).bind(batiUsersDetailsModal, batiChatMsgModel, batiUserChatsModal) ;
//        ((PostViewHolder) holder).bind(chatsModal, chatsMsgModal) ;
//        ((PostViewHolder) holder).bind(chatsModal) ;




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



        public void multiBind(BatiUserChatsModal batiUserChatsModal ) {

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











        public void superBind(List<BatiUsersDetailsModal> mUserDetailsModalList, List<BatiChatMsgModel> mChatsMsgList, List<BatiUserChatsModal> mBatiUserChatsList) {

            for (int i=0; i<mBatiUserChatsList.size() ; i++){
//                Log.e("doc_id", "chat id:  "+ mChatsMsgList.get(i).getChatId() ) ;
                Log.e("doc_id", "userChats id:  "+ mBatiUserChatsList.get(i).getDocumentId() ) ;
                for (int j=0; j<mUserDetailsModalList.size(); j++){
                    if(mUserDetailsModalList.get(j).getDocumentId().equalsIgnoreCase(mBatiUserChatsList.get(i).getDocumentId() ) ){
                        Log.e("doc_id", "details id:  "+ mUserDetailsModalList.get(j).getDocumentId() ) ;
                        Log.e("doc_id", "details id:  "+ mUserDetailsModalList.get(j).getName() ) ;

                        AppImageLoader.loadImageInView(mUserDetailsModalList.get(j).getPhoto(), R.drawable.profile_image, (ImageView)mUserImageLogo);

                        textViewUserName.setText(mUserDetailsModalList.get(j).getName());
                        textViewUserStatus.setText(mChatsMsgList.get(i).getMessage());
                    }
                }
            }


        }



















        public void bind( final BatiUsersDetailsModal usersDetailsModal, final BatiChatMsgModel chatsMsgModal, final BatiUserChatsModal batiUserChatsModal  ){
//            Timestamp timestamp = (Timestamp) products.getLastupdatetime();
//            Date date = timestamp.toDate();
//            CharSequence dateFormat = DateFormat.format("yyyy-MM-dd hh:mm:ss a", date);

            AppImageLoader.loadImageInView(usersDetailsModal.getPhoto(), R.drawable.profile_image, (ImageView)mUserImageLogo);

            textViewUserName.setText(usersDetailsModal.getName());
            Log.e("chat_list_msg", "id :  "+ usersDetailsModal.getDocumentId()+ " msgId: "+ chatsMsgModal.getChatId() ) ;
//            if(usersDetailsModal.getDocumentId().equalsIgnoreCase(batiUserChatsModal.getDocumentId())){
                textViewUserStatus.setText(chatsMsgModal.getMessage());
                Log.e("chat_list_msg", "msg :  "+ chatsMsgModal.getMessage() ) ;
//            }

            Log.e("chat_list_chat", "unseen msg:  "+ batiUserChatsModal.getUnseen_message() ) ;
            Log.e("chat_list_chat", "unseen id:  "+ batiUserChatsModal.getDocumentId() ) ;
            Log.e("chat_list_chat", "chat id:  "+ usersDetailsModal.getDocumentId() ) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mCtx, FirestoreUserChatsActivity.class) ;
                    intent.putExtra("chatID", usersDetailsModal.getDocumentId() ) ;
                    intent.putExtra("chat_name", usersDetailsModal.getName() ) ;
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mCtx.startActivity(intent);
//                    Toast.makeText(mCtx, "chatID: "+ userDetails.getDocumentId(), Toast.LENGTH_SHORT).show();
                }
            });

        }



    }
}