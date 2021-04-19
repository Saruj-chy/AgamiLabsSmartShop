package com.agamilabs.smartshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.model.SmothPagerModel;
import com.astritveliu.boom.Boom;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static com.av.smoothviewpager.utils.Smoolider_Utils.decodeSampledBitmapFromResource;

public class SmothPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<SmothPagerModel> feedItemList;

    public SmothPagerAdapter(List<SmothPagerModel> feedItemList, Context mContext) {
        this.mContext = mContext;
        this.feedItemList = feedItemList;
    }

    @Override
    public int getCount() {
        return feedItemList.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        final SmothPagerModel smothCard = feedItemList.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_smoth_pager, container, false);

        ImageView img_slider = (ImageView) view.findViewById(R.id.img_slider);
        TextView mDetailsText =  view.findViewById(R.id.text_details);

        mDetailsText.setText("details about "+ smothCard.getName());
        new Boom(mDetailsText);

        img_slider.setImageBitmap(decodeSampledBitmapFromResource(mContext.getResources(),smothCard.getImageUrl(), 800, 650));

        img_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Slider action
                Snackbar.make(v, "Click the pin for more options", Snackbar.LENGTH_LONG).show();
            }
        });

        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem (ViewGroup container, int position, Object object){
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject (View view, Object object){
        return view.equals(object);
    }
}