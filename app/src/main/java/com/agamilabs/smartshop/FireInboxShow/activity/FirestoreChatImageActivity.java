package com.agamilabs.smartshop.FireInboxShow.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.agamilabs.smartshop.FireInboxShow.FirestoreChatsImageFragment;
import com.agamilabs.smartshop.FireInboxShow.Interface.onZoomOutInterface;
import com.agamilabs.smartshop.FireInboxShow.adapter.ViewPagerAdapter;
import com.agamilabs.smartshop.R;

import java.util.ArrayList;
import java.util.List;

public class FirestoreChatImageActivity extends AppCompatActivity {

    private static boolean zoomOutState;
    List<String> realList ;
    String position, sentBy ;
    String sentByName ;

    ViewPager mChatImageViewPager ;
    ViewPagerAdapter adapterViewPager ;
    Toolbar toolbar ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestore_chat_image);


        Bundle args = getIntent().getBundleExtra("bundle");
        realList = (ArrayList<String>) args.getSerializable("real_image_list");
        position = getIntent().getStringExtra("position") ;
        sentBy = getIntent().getStringExtra("sentBy") ;

        if(sentBy.equalsIgnoreCase(FireStoreUserActivity.USER_ID)){
            sentByName = FireStoreUserActivity.mUserName ;
        }else{
            sentByName = FirestoreUserChatsActivity.mChatUserName ;
        }


        toolbar = findViewById(R.id.appbar_chat_image);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(sentByName);


        mChatImageViewPager = findViewById(R.id.view_pager_chat_image) ;
        adapterViewPager = new ViewPagerAdapter(getSupportFragmentManager(), realList);
        mChatImageViewPager.setAdapter(adapterViewPager);

        mChatImageViewPager.setCurrentItem(Integer.parseInt(position));
        mChatImageViewPager.setOffscreenPageLimit(3);

        mChatImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("number", "position:"+ position ) ;
                getSupportActionBar().setSubtitle( (position+1)+ " of "+ realList.size() +" photos"   );
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


}