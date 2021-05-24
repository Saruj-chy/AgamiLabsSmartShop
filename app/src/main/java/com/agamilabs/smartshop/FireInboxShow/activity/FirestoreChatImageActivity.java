package com.agamilabs.smartshop.FireInboxShow.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.agamilabs.smartshop.FireInboxShow.FirestoreChatsImageAdapter;
import com.agamilabs.smartshop.FireInboxShow.OnIntentUrl;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FirestoreChatImageActivity extends AppCompatActivity implements OnIntentUrl {
    private DocumentReference userChatMsgRef;

    RecyclerView mChatImageRV ;
    FirestoreChatsImageAdapter firestoreChatsImageAdapter ;

    List<String> thumbArrayList;
    List<String> realArrayList;
    List<String> thumbList;
    List<String> realList;

    String thumbString, realString, mChatId, mDocumentId ;


    //FrameLayout
    FrameLayout mFrameLayout ;
    ImageView mFrameImageView;
    ImageButton mFrameImgBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestore_chat_image);

        userChatMsgRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("chatMessages");


        Bundle args = getIntent().getBundleExtra("BUNDLE");
        ArrayList<String> object = (ArrayList<String>) args.getSerializable("ARRAYLIST");
        ArrayList<String> object2 = (ArrayList<String>) args.getSerializable("ARRAYLIST2");

        Log.e("object", "object: "+object  ) ;
        Log.e("object", "object2: "+object2  ) ;

        thumbString = getIntent().getStringExtra("thumbList") ;
        realString = getIntent().getStringExtra("realList") ;
        mChatId = getIntent().getStringExtra("chatId") ;
        mDocumentId = getIntent().getStringExtra("documentId") ;

        Log.e("chat_id", "mChatId: "+mChatId+ "   mDocumentId:"+ mDocumentId   ) ;

//        String replaceThumbString = realString.replace("[","");
//        String replaceThumbString1 = replaceThumbString.replace("]","");
//        List<String> myList1 = new ArrayList<String>(Arrays.asList(replaceThumbString1.split(",")));



        mChatImageRV = findViewById(R.id.recycler_firestore_fragment);
        Toolbar toolbar = findViewById(R.id.appbar_chat_image);
        mFrameLayout = findViewById(R.id.frame_layout) ;
        mFrameImageView = findViewById(R.id.frame_img_view) ;
        mFrameImgBtn = findViewById(R.id.frame_img_btn) ;


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(chatName);


        thumbList = new ArrayList<>();
        realList = new ArrayList<>();



        initializeAdapter() ;
//        realList = (ArrayList<String>) args.getSerializable("ARRAYLIST");
//        thumbList = (ArrayList<String>) args.getSerializable("ARRAYLIST2");
//        Log.e("object", "realList: "+realList.size()  ) ;
//        firestoreChatsImageAdapter.notifyDataSetChanged();


        loadImageArrayList(mChatId);



//        thumbArrayList = returnListFromString(thumbString);
//        realArrayList = returnListFromString(realString);
//
//
//
//
////        for(int i=0; i<myList1.size(); i++){
////            realList.add(myList1.get(i)) ;
//////
////        }
//        Log.e("size", "realList:"+realList.size()  );
//
////        realList = returnListFromString(realString);
//
//
//
//
//
//        for(int i = 0; i< thumbArrayList.size(); i++){
//            thumbList.add( thumbList.size(), thumbArrayList.get(i) );
//            realList.add( realList.size(), realArrayList.get(i) );
//        }
//        Log.e("size", "realList size:"+realList.size()  );
//
//        firestoreChatsImageAdapter.notifyDataSetChanged();







    }

    private void loadImageArrayList(String mChatId) {

        userChatMsgRef.collection(mChatId).document(mDocumentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        HashMap<String, Object> imageMapList = (HashMap<String, Object>) documentSnapshot.get("imageList");
                        List<String> imageRealList = new ArrayList<>();
                        List<String> imageThumbList = new ArrayList<>();
                        if (imageMapList != null) {
                            imageRealList = (List<String>) imageMapList.get("real");
                            imageThumbList = (List<String>) imageMapList.get("thumb");
                        }

                        for(int i=0; i<imageThumbList.size(); i++){
                            thumbList.add( imageThumbList.get(i) );
                            realList.add( imageRealList.get(i) );
                        }


                        firestoreChatsImageAdapter.notifyDataSetChanged();

                        Log.e("image_list", "realList:"+realList.size()  );
                        Log.e("image_list", "thumbList:"+thumbList.size()  );

                    } else {
                    }
                } else {
                }
            }
        });

//        Log.e("image_list", "realList:"+realList.size()  );
//        Log.e("image_list", "thumbList:"+thumbList.size()  );


    }

    private List<String> returnListFromString(String stringName) {
//        String stringExtra = getIntent().getStringExtra(stringName) ;

        String replaceThumbString1 = stringName.replace("[","");
        String replaceThumbString = replaceThumbString1.replace("]","");
        List<String> myList = new ArrayList<String>(Arrays.asList(replaceThumbString.split(",")));

        return myList;
    }

    private void initializeAdapter() {
        mChatImageRV.setHasFixedSize(true);
        firestoreChatsImageAdapter = new FirestoreChatsImageAdapter(getApplicationContext(), thumbList,realList, this);
        mChatImageRV.setAdapter(firestoreChatsImageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext()) ;
        mChatImageRV.setLayoutManager(linearLayoutManager);

//        GridLayoutManager manager1 = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.HORIZONTAL, false);
//        mChatImageRV.setLayoutManager(manager1);  // set horizontal LM


    }

    public void onClearFrameClick(View view) {
        mFrameLayout.setVisibility(View.GONE);
        mChatImageRV.setAlpha((float) 1.0);
    }
    @Override
    public void onIntentUrl(String URL) {
            mFrameLayout.setVisibility(View.VISIBLE);
            mChatImageRV.setAlpha((float) 0.1);

            AppImageLoader.loadImageInView(URL, R.drawable.profile_image, (ImageView) mFrameImageView);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);







    }
}