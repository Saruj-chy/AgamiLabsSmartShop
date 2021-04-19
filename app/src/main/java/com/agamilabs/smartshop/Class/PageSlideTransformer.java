package com.agamilabs.smartshop.Class;

import android.util.Log;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.agamilabs.smartshop.controller.AppController;

public class PageSlideTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

//        Log.e("TAG", position+"    position");
        if (-1 < position && position < 0) {
            float scaleFactor = 1 - Math.abs(position) * 0.1f;
            float verticalMargin = pageHeight * (1 - scaleFactor) / 2;
            float horizontalMargin = pageWidth * (1 - scaleFactor) / 2;

            AppController.getAppController().getInAppNotifier().log("TAG", scaleFactor+"  scaleFactor");
            AppController.getAppController().getInAppNotifier().log("TAG", verticalMargin+"  verticalMargin");
            AppController.getAppController().getInAppNotifier().log("TAG", horizontalMargin+"  horizontalMargin");
            if (position < 0) {
                view.setTranslationX(horizontalMargin - verticalMargin / 2);
            } else {
                view.setTranslationX(-horizontalMargin + verticalMargin / 2);
            }
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }

        view.setTranslationX(view.getWidth() * -position);

        if (position > 0) {
            float xPosition = position * view.getWidth();
            view.setTranslationX(xPosition);

        }

//        else if (position < 0) {
//            float xPosition = position * view.getWidth()-100;
//            view.setTranslationX(xPosition);
//
//        }
    }
}