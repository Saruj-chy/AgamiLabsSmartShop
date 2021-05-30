package com.agamilabs.smartshop.FireInboxShow.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.FireInboxShow.Interface.OnIntentUrl;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppImageLoader;

import java.util.List;

public class FirestoreChatsImageAdapter extends RecyclerView.Adapter<FirestoreChatsImageAdapter.PostViewHolder> {
    private Context mCtx;
    private List<String> mThumbImgtList ;
    private List<String> mRealImgtList ;
    private OnIntentUrl onIntentUrl;



    public FirestoreChatsImageAdapter(Context mCtx, List<String> mThumbImgtList, List<String> mRealImgtList, OnIntentUrl onIntentUrl) {
        this.mCtx = mCtx;
        this.mThumbImgtList = mThumbImgtList;
        this.mRealImgtList = mRealImgtList;
        this.onIntentUrl = onIntentUrl;
        notifyDataSetChanged();
    }

    @Override
    public FirestoreChatsImageAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_full_image, null);
        return new FirestoreChatsImageAdapter.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final FirestoreChatsImageAdapter.PostViewHolder holder, final int position) {
        final String mImgThumbList = mThumbImgtList.get(position);
        final String mImgRealList = mRealImgtList.get(position);

        ((FirestoreChatsImageAdapter.PostViewHolder) holder).bind(mImgThumbList, mImgRealList, position);

    }

    @Override
    public int getItemCount() {
        return mRealImgtList.size();
    }


    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        public PostViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_full_layout);
        }

        public void bind(String mImgThumbList, String mImgRealList, int position) {
            Log.e("real_list", position+"  mImgRealList:"+mImgRealList ) ;
            AppImageLoader.loadImageInView(mImgRealList, R.drawable.facebook, imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onIntentUrl.onIntentUrl(mImgRealList);
                }
            });

//            Picasso.get().load(mImgRealList)
//                    .placeholder(R.drawable.profile_image)
//                    .into(imageView);
        }


    }
}