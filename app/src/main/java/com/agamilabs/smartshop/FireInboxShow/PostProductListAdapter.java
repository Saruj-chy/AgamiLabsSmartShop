package com.agamilabs.smartshop.FireInboxShow;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.BatikromUserMsgModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;

import java.util.Date;
import java.util.List;

public class PostProductListAdapter extends RecyclerView.Adapter<PostProductListAdapter.PostViewHolder> {


    private Context mCtx;
    private List<BatikromUserMsgModel> productList;
    private CollectionReference productRef ;
    private String currentUserId;

    public PostProductListAdapter(Context mCtx, List<BatikromUserMsgModel> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public PostProductListAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_batikrom_uer_list, null);
        currentUserId = FirebaseAuth.getInstance().getUid();
        return new PostProductListAdapter.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final PostProductListAdapter.PostViewHolder holder, final int position) {
        final BatikromUserMsgModel products = productList.get(position);

        ((PostViewHolder) holder).bind(products) ;


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDocId, textViewUnseenMsg, textViewUpdateTime;
        ImageButton deleteButton ;



        public PostViewHolder(View itemView) {
            super(itemView);

            textViewDocId = itemView.findViewById(R.id.text_document_id);
            textViewUnseenMsg = itemView.findViewById(R.id.text_unseen_msg);
            textViewUpdateTime = itemView.findViewById(R.id.text_update_time);




        }

        public void bind(final BatikromUserMsgModel products){
            Timestamp timestamp = (Timestamp) products.getLastupdatetime();
            Date date = timestamp.toDate();
            CharSequence dateFormat = DateFormat.format("yyyy-MM-dd hh:mm:ss a", date);


            textViewDocId.setText(products.getDocumentId());
            textViewUnseenMsg.setText(products.getUnseen_message()+"");
            textViewUpdateTime.setText(dateFormat);

        }

    }
}