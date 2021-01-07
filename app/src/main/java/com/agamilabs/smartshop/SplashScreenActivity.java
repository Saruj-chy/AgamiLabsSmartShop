package com.agamilabs.smartshop;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

public class SplashScreenActivity extends AppCompatActivity {
    ProgressBar splashProgress;
    int SPLASH_TIME = 3000;

    private boolean action = false;

    int count =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE) ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        splashProgress = findViewById(R.id.splashProgress);
        playProgress();

        Button entryBtn = findViewById(R.id.entryBtn) ;
        entryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mySuperIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mySuperIntent);
                count = 1;
                SPLASH_TIME = 0 ;
                finish();
                action = true ;
                Log.d("TAG", "action1: "+action) ;
            }
        });




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(action==false){
                    Log.d("TAG", "action2: "+action) ;
                    Intent mySuperIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mySuperIntent);

                    finish();
                }


            }
        }, SPLASH_TIME);

        Log.d("TAG", "action: "+action) ;
        Log.d("TAG", "splashtime: "+SPLASH_TIME) ;

    }

    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(SPLASH_TIME)
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SPLASH_TIME = 1000;
    }
}