package com.agamilabs.smartshop.FireInboxShow;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FireStoreUserAdapter extends RecyclerView.Adapter<FireStoreUserAdapter.PostViewHolder> {


    private Context mCtx;
    private List<BatiUsersDetailsModal> mUserDetailsModalList;

    public FireStoreUserAdapter(Context mCtx,  List<BatiUsersDetailsModal> mUserDetailsModalList) {
        this.mCtx = mCtx;
        this.mUserDetailsModalList = mUserDetailsModalList;
    }

    @Override
    public FireStoreUserAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_firestore_inbox_user, null);
        return new FireStoreUserAdapter.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final FireStoreUserAdapter.PostViewHolder holder, final int position) {
        final BatiUsersDetailsModal chatsModal = mUserDetailsModalList.get(position);

        ((PostViewHolder) holder).bind(chatsModal) ;




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

        public void bind( final BatiUsersDetailsModal userDetails){
//            Timestamp timestamp = (Timestamp) products.getLastupdatetime();
//            Date date = timestamp.toDate();
//            CharSequence dateFormat = DateFormat.format("yyyy-MM-dd hh:mm:ss a", date);

            AppImageLoader.loadImageInView(userDetails.getPhoto(), R.drawable.profile_image, (ImageView)mUserImageLogo);

            textViewUserName.setText(userDetails.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mCtx, FirestoreUserChatsActivity.class) ;
                    intent.putExtra("chatID", userDetails.getDocumentId()) ;
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mCtx.startActivity(intent);
//                    Toast.makeText(mCtx, "chatID: "+ userDetails.getDocumentId(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
}