package com.agamilabs.smartshop.FireInboxShow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.activity.StockReportActivity;
import com.google.type.Date;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FireStoreUserAdapter extends RecyclerView.Adapter<FireStoreUserAdapter.PostViewHolder> {


    private Context mCtx;
    private List<BatiUserChatsModal> mUserChatsModalList;
//    private List<BatiChatsModal> mChatsModalList;
    private List<BatiUsersDetailsModal> mUserDetailsModalList;

    public FireStoreUserAdapter(Context mCtx, List<BatiUserChatsModal> mUserChatsModalList, List<BatiUsersDetailsModal> mUserDetailsModalList) {
        this.mCtx = mCtx;
        this.mUserDetailsModalList = mUserDetailsModalList;
        this.mUserChatsModalList = mUserChatsModalList;
//        AppController.getAppController().getInAppNotifier().log("BatikromUserAdapter", "BatikromUserAdapter: " + mUserChatsModalList);
    }

    @Override
    public FireStoreUserAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_firestore_inbox_user, null);
        return new FireStoreUserAdapter.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final FireStoreUserAdapter.PostViewHolder holder, final int position) {
        final BatiUserChatsModal userChatsModal = mUserChatsModalList.get(position);
        final BatiUsersDetailsModal chatsModal = mUserDetailsModalList.get(position);

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

        public void bind(final BatiUserChatsModal userChats , final BatiUsersDetailsModal chats){
//            Timestamp timestamp = (Timestamp) products.getLastupdatetime();
//            Date date = timestamp.toDate();
//            CharSequence dateFormat = DateFormat.format("yyyy-MM-dd hh:mm:ss a", date);

            textViewUserName.setText(chats.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mCtx, FirestoreUserChatsActivity.class) ;
                    intent.putExtra("chatID", userChats.getDocumentId()) ;
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mCtx.startActivity(intent);
                }
            });

        }

    }
}