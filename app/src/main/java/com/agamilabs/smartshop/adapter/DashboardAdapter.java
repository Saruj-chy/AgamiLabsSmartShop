package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.FullScannerActivity;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.ShopAdminHome;
import com.agamilabs.smartshop.SplashScreenActivity;
import com.agamilabs.smartshop.activity.CampaignActivity;
import com.agamilabs.smartshop.activity.OrderReportActivity;
import com.agamilabs.smartshop.activity.RechargeActivity;
import com.agamilabs.smartshop.activity.ShopInboxActivity;
import com.agamilabs.smartshop.activity.StockReportActivity;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.SmothPagerModel;
import com.agamilabs.smartshop.model.StockReportModel;

import java.util.List;


public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mCtx;
    private List<SmothPagerModel> mItemList;


    public DashboardAdapter(Context mCtx, List<SmothPagerModel> mItemList) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dashboard_card_list, null);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        SmothPagerModel mPagerList = mItemList.get(position) ;

        ((ViewHolder) holder).bind(mPagerList, position);

        AppController.getAppController().getInAppNotifier().log("response", "adapter");
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

      TextView  mName ;
      ImageView mImageLogo ;

        public ViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.text_shop_name) ;
            mImageLogo = itemView.findViewById(R.id.image_logo) ;

        }

        public void bind(SmothPagerModel mItemArray, int position) {

            mName.setText(mItemArray.getName());
            mImageLogo.setImageResource(mItemArray.getImageUrl());

            mImageLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    AppController.getAppController().getInAppNotifier().showToast("itemview clicked ");
                    switch (mItemArray.getName()){
                        case "New Sale":
                            mCtx.startActivity(new Intent(mCtx, FullScannerActivity.class));
                            break;
                        case "Sale Invoices":
//                    mCtx.startActivity(new Intent(mCtx, Sal.class));
                            break;
                        case "Stock Report":
                            mCtx.startActivity(new Intent(mCtx, StockReportActivity.class));
                            break;
                        case "Online Orders":
                            mCtx.startActivity(new Intent(mCtx, OrderReportActivity.class));
                            break;
                        case "Shop Inbox":
                            mCtx.startActivity(new Intent(mCtx, ShopInboxActivity.class));
                            break;
                        case "Campaign":
                            mCtx.startActivity(new Intent(mCtx, CampaignActivity.class));
                            break;
                        case "Recharge":
                            mCtx.startActivity(new Intent(mCtx, RechargeActivity.class));
                            break;
                        default:
                            break;
                    }
                }
            });


        }
    }
}