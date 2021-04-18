package com.agamilabs.smartshop.FireInboxShow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BatikromUserAdapter extends RecyclerView.Adapter<BatikromUserAdapter.PostViewHolder> {


    private Context mCtx;
    private List<BatiUserChatsModal> mUserChatsModalList;
    private List<BatiChatsModal> mChatsModalList;

    public BatikromUserAdapter(Context mCtx, List<BatiUserChatsModal> mUserChatsModalList, List<BatiChatsModal> mChatsModalList) {
        this.mCtx = mCtx;
        this.mChatsModalList = mChatsModalList;
        this.mUserChatsModalList = mUserChatsModalList;
//        AppController.getAppController().getInAppNotifier().log("BatikromUserAdapter", "BatikromUserAdapter: " + mUserChatsModalList);
    }

    @Override
    public BatikromUserAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_firestore_inbox_user, null);
        return new BatikromUserAdapter.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final BatikromUserAdapter.PostViewHolder holder, final int position) {
        final BatiUserChatsModal userChatsModal = mUserChatsModalList.get(position);
        final BatiChatsModal chatsModal = mChatsModalList.get(position);

        ((PostViewHolder) holder).bind(userChatsModal, chatsModal) ;


    }

    @Override
    public int getItemCount() {
        return mUserChatsModalList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUserName, textViewUserStatus ;
        CircleImageView mUserImageLogo ;



        public PostViewHolder(View itemView) {
            super(itemView);

            textViewUserName = itemView.findViewById(R.id.text_user_name);
            textViewUserStatus = itemView.findViewById(R.id.text_user_status);

        }

        public void bind(final BatiUserChatsModal userChats , final BatiChatsModal chats){
//            Timestamp timestamp = (Timestamp) products.getLastupdatetime();
//            Date date = timestamp.toDate();
//            CharSequence dateFormat = DateFormat.format("yyyy-MM-dd hh:mm:ss a", date);


//            textViewDocId.setText(products.getDocumentId());
//            textViewUnseenMsg.setText(products.getUnseen_message()+"");
//            textViewUpdateTime.setText(dateFormat);

//            if(userChats.getDocumentId() == chats.getUserChatId()){
//                textViewUserName.setText(userChats.getDocumentId());
//            }
            textViewUserName.setText(chats.getUsersList().get(1));
            textViewUserStatus.setText(chats.getUsersList().get(0));
//            AppController.getAppController().getInAppNotifier().log("BatikromUserAdapter", "userChats: "+ userChats+"   chats:  "+chats );


//            textViewDocId.setText(userChats.getUserChatId());
//            textViewUnseenMsg.setText(userChats.getUsersList().get(0));
//            textViewUpdateTime.setText(userChats.getUsersList().get(1));

        }

    }
}