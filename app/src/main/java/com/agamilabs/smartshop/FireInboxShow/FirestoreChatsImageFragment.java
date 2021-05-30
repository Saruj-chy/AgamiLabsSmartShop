package com.agamilabs.smartshop.FireInboxShow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agamilabs.smartshop.FireInboxShow.activity.FirestoreChatImageActivity;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class FirestoreChatsImageFragment extends Fragment {

    private String url;
    private int position;
    private float zoomNumber ;
//    private ImageView mChatImageView;
    private TouchImageView mChatImageView2;
    private ProgressBar mChatProgress ;






    public static FirestoreChatsImageFragment newInstance(int position, String url, float zoomNumb) {
        FirestoreChatsImageFragment fragmentFirst = new FirestoreChatsImageFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("url", url);
        args.putFloat("zoomNumb", zoomNumb);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position", 0);
        url = getArguments().getString("url");
        zoomNumber = getArguments().getFloat("zoomNumb");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firestore_chats_image, container, false);
//        mChatImageView = (ImageView) view.findViewById(R.id.image_chat_fragment);
        mChatImageView2 =  (TouchImageView) view.findViewById(R.id.image_chat_fragment2);
        mChatProgress = (ProgressBar) view.findViewById(R.id.progress_fragment);

//        AppImageLoader.loadImageInView(url, R.drawable.profile_image, mChatImageView);
        AppImageLoader.loadImageInView(url, R.drawable.profile_image, mChatImageView2);


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                Log.e("keyCode","keyCode:"+mChatImageView2.isZoomed()  ) ;
                if(!mChatImageView2.isZoomed()){
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return false;
                }else{
                    mChatImageView2.resetZoom();
                    return true;
                }

//                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                return false;

//                Log.e("keyCode","keyCode:"+keyCode  ) ;
//                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//                    Log.e("keyCode","Onkey backlistener:"+keyCode  ) ;
//                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    return true;
//                }
//                return false;
            }
        });





        return view;
    }


}