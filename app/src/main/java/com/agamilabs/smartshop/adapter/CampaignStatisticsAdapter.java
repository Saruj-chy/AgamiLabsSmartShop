package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.CampaignStatItem;

import java.util.ArrayList;

public class CampaignStatisticsAdapter extends RecyclerView.Adapter<CampaignStatisticsAdapter.CampaignStatisticsViewHolder>  {
    private Context mContext;
    private ArrayList<CampaignStatItem> mCampaignStatList;
    private String title,number;

    public CampaignStatisticsAdapter(Context mContext, ArrayList<CampaignStatItem> mCampaignStatList) {
        this.mContext = mContext;
        this.mCampaignStatList = mCampaignStatList;
    }

    @NonNull
    @Override
    public CampaignStatisticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_campaign_statistics, parent, false);
        return new CampaignStatisticsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CampaignStatisticsViewHolder holder, int position) {
        CampaignStatItem currentItem = mCampaignStatList.get(position);

        title = currentItem.getTitle();
        number = currentItem.getNumber();

        holder.text_title.setText(title);
        holder.text_number.setText(number);

    }

    @Override
    public int getItemCount() {
        return mCampaignStatList.size();
    }


    public class CampaignStatisticsViewHolder extends RecyclerView.ViewHolder {
        public TextView text_title,text_number;

        public CampaignStatisticsViewHolder(@NonNull View itemView) {
            super(itemView);
            text_title = itemView.findViewById(R.id.text_title);
            text_number = itemView.findViewById(R.id.text_number);

        }
    }
}
