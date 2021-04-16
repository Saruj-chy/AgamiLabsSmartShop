package com.agamilabs.smartshop.Interfaces;

import android.view.View;

import com.agamilabs.smartshop.model.CategoryStockReportModel;

public interface OnClickInterface {

    void onListItemSelected(CategoryStockReportModel categoryStockReportModel, int position, boolean isLongClick ) ;
//    int onPosition(int position) ;

}
