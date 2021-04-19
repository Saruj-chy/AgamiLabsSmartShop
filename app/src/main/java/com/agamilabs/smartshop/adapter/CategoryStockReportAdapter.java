package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.OnClickInterface;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.CategoryStockReportModel;
import com.agamilabs.smartshop.model.StockReportModel;

import java.math.BigDecimal;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CategoryStockReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mCtx;
    private List<CategoryStockReportModel> mItemList;
    private OnClickInterface onClickInterface ;

    public CategoryStockReportAdapter(Context mCtx, List<CategoryStockReportModel> mItemList, OnClickInterface onClickInterface ) {
        this.mCtx = mCtx;
        this.mItemList = mItemList;
        this.onClickInterface = onClickInterface;
    }

    //product search
    public void searchFilterList(List<CategoryStockReportModel> filteredList) {
        mItemList = filteredList;
        notifyDataSetChanged();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_category_list, null);
        return new CategoryStockReportAdapter.StockReportViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CategoryStockReportModel mCategoryList = mItemList.get(position);

        ((CategoryStockReportAdapter.StockReportViewHolder) holder).bind(mCategoryList, position);


    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class StockReportViewHolder extends RecyclerView.ViewHolder  {

        TextView mCategoryName ;


        public StockReportViewHolder(View itemView) {
            super(itemView);

            mCategoryName = itemView.findViewById(R.id.text_category_name);

           }

        public void bind(CategoryStockReportModel mCategoryList, int position) {
            mCategoryName.setText(mCategoryList.getCattext());

            mCategoryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickInterface.onListItemSelected(mCategoryList, position, false);
                }
            });
        }


    }
}