package com.agamilabs.smartshop.aaaaaaaaaa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.agamilabs.smartshop.R;

public class AllSiteActivity extends AppCompatActivity {

    private ImageView mFBImage, mInstragramImage, mSkypeImage, mTwitterImage, mYoutubeImage, mSmartShopImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_site);

        Initialize() ;

        mFBImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TestingSessionActivity.class);
                i.putExtra("site_url", "https://www.facebook.com/");
                startActivity(i);
            }
        });

        mInstragramImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TestingSessionActivity.class);
                i.putExtra("site_url", "https://www.instagram.com/");
                startActivity(i);
            }
        });

        mSkypeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TestingSessionActivity.class);
                i.putExtra("site_url", "https://www.skype.com/");
                startActivity(i);
            }
        });

        mTwitterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TestingSessionActivity.class);
                i.putExtra("site_url", "https://twitter.com/");
                startActivity(i);
            }
        });

        mYoutubeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TestingSessionActivity.class);
                i.putExtra("site_url", "https://www.youtube.com/");
                startActivity(i);
            }
        });

        mSmartShopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TestingSessionActivity.class);
                i.putExtra("site_url", "https://www.smartshop.com/");
                startActivity(i);
            }
        });


    }

    private void Initialize() {
        mFBImage = findViewById(R.id.image_facebook) ;
        mInstragramImage = findViewById(R.id.image_instragram) ;
        mSkypeImage = findViewById(R.id.image_skype) ;
        mTwitterImage = findViewById(R.id.image_twitter) ;
        mYoutubeImage = findViewById(R.id.image_youtube) ;
        mSmartShopImage = findViewById(R.id.image_smart_shop) ;
    }
}