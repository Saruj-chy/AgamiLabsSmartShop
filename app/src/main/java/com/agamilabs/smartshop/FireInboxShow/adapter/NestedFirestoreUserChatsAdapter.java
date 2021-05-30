package com.agamilabs.smartshop.FireInboxShow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.FireInboxShow.Interface.OnIntentUrl;
import com.agamilabs.smartshop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NestedFirestoreUserChatsAdapter extends RecyclerView.Adapter<NestedFirestoreUserChatsAdapter.PostViewHolder> {
    private Context mCtx;
    private List<String> mThumbImgtList ;
    private List<String> mRealImgtList ;
    private OnIntentUrl onIntentUrl;



    public NestedFirestoreUserChatsAdapter(Context mCtx, List<String> mThumbImgtList, List<String> mRealImgtList, OnIntentUrl onIntentUrl) {
        this.mCtx = mCtx;
        this.mThumbImgtList = mThumbImgtList;
        this.mRealImgtList = mRealImgtList;
        this.onIntentUrl = onIntentUrl;
    }

    @Override
    public NestedFirestoreUserChatsAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.nested_post_image, null);
        return new NestedFirestoreUserChatsAdapter.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final NestedFirestoreUserChatsAdapter.PostViewHolder holder, final int position) {
        final String mImgThumbList = mThumbImgtList.get(position);
        final String mImgRealList = mRealImgtList.get(position);

        ((NestedFirestoreUserChatsAdapter.PostViewHolder) holder).bind(mImgThumbList, mImgRealList);

    }

    @Override
    public int getItemCount() {
        return mThumbImgtList.size();
    }


    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        private AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mCtx);
        private AlertDialog dialog;

        public PostViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.nested_imageView);
        }

        public void bind(String thumbImgUrl, String realImgUrl){
            if(!thumbImgUrl.isEmpty()){
                Picasso.get().load(thumbImgUrl)
                        .placeholder(R.drawable.profile_image)
                        .into(imageView);
            }


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    View dialogView = inflater.inflate(R.layout.image_popup, null);
//                    ImageView imageView_pop = dialogView.findViewById(R.id.image_pop) ;
//                    imageView_pop.setImageResource(R.drawable.profile_image);
//                    dialogBuilder.setView(view);
//                    dialog = dialogBuilder.create();
//                    dialog.show();

                    onIntentUrl.onIntentUrl(realImgUrl);

//                    Toast.makeText(mCtx, "real image url:"+realImgUrl, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}