package com.agamilabs.smartshop.FireInboxShow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agamilabs.smartshop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirestoreUserChatsActivity extends AppCompatActivity {

    String chatId, chatName;
    private CollectionReference userChatMsgRef ;

    private ProgressBar mChatProgressbar ;
    private RecyclerView mChatMsgRV ;
    private LinearLayoutManager linearLayoutManager;
    protected FireStoreUserChatsAdapter mUserChatsAdapter ;
    private List<BatiChatMsgModel> mChatsMsgList = new ArrayList<>();
    private int firstVisiblesItems, dataExistNum=0;
    private boolean loading = true ;


    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 2000 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestore_user_chats);

        chatId =  getIntent().getStringExtra("chatID");
        chatName =  getIntent().getStringExtra("chat_name");

        Toolbar toolbar = findViewById(R.id.firestore_user_chats_appbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(chatName);


        userChatMsgRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("chatMessages").collection(chatId);

        mChatMsgRV = findViewById(R.id.recycler_chatmsg) ;
        mChatProgressbar = findViewById(R.id.progress_chat_firestore) ;



        initializeAdapter();
        loadChatMsgArrayCollection() ;


//        loadNextFirestoreData();



    }

    private void loadScrollViewRV(Query next) {
        Log.e("notify", "notify: " ) ;

        mChatMsgRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("RV", "dx: "+dx+"  dy:  "+dy ) ;
                if(dy<0){
                    firstVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                   if(firstVisiblesItems == 0 && loading){
                       loading=false;
                       mChatProgressbar.setVisibility(View.VISIBLE);

                       setAutoRefresh(next);
//                           Toast.makeText(FirestoreUserChatsActivity.this, "length: "+ mChatsMsgList.size(), Toast.LENGTH_SHORT).show();
                   }
                }

            }
        });
    }
    private void setAutoRefresh(Query next){
        //if already countdowntime nul na hole, countdowntimer k stop korbe.
        //max refresh time 0 or 0 theke chotu hoi, uporer kaj ta korbe..
        if(remainingRefreshTime<=0){
            if(countDownTimer!= null){
                countDownTimer.cancel();
            }
            return;

        }
        if(countDownTimer == null){
            countDownTimer = new CountDownTimer(remainingRefreshTime, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                   Log.e("trick", "trick: "+ remainingRefreshTime) ;
                }

                @Override
                public void onFinish() {
                    Log.e("trick", "finish: "+ remainingRefreshTime) ;
                    mChatProgressbar.setVisibility(View.GONE);
                    loadNextFirestoreData(next);
                    cancelAutoRefresh() ;

                }
            };

            countDownTimer.start() ;
        }
    }
    private void cancelAutoRefresh(){
        if(countDownTimer!= null){
            countDownTimer.cancel();
            countDownTimer=null;
        }
    }



    private void loadNextFirestoreData(Query next){
        next.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if(documentSnapshots.size()<=0){
                            return;
                        }
                        DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);

                        Query next =  userChatMsgRef.orderBy("sentTime", Query.Direction.DESCENDING)
                                .startAfter(lastVisible)
                                .limit(5);
                        next.get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot documentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {
                                            BatiChatMsgModel chatMsgModel = documentSnapshot.toObject(BatiChatMsgModel.class);

                                            dataExistNum = documentSnapshots.size() ;
                                            mChatsMsgList.add(new BatiChatMsgModel(
                                                    documentSnapshot.getId(),
                                                    chatMsgModel.getMessage(),
                                                    chatMsgModel.getSentBy(),
                                                    chatMsgModel.getSentTime()
                                            ));
                                        }
                                        mUserChatsAdapter.notifyDataSetChanged();
                                        Log.e("trick_num", "dataExistNum: "+ dataExistNum) ;
                                        linearLayoutManager.scrollToPositionWithOffset(dataExistNum-1,  0);
//                                        mUserChatsAdapter.notifyItemChanged(previousTotal+5);
//                                        Log.e("previous","previousTotal :  "+ previousTotal  ) ;
                                    }
                                });

                        loading = true ;
                        loadScrollViewRV(next);
                    }
                });
    }



    private void initializeAdapter() {
        mChatMsgRV.setHasFixedSize(true);
        mUserChatsAdapter = new FireStoreUserChatsAdapter(getApplicationContext(), mChatsMsgList);
        mChatMsgRV.setAdapter(mUserChatsAdapter);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext()) ;
        linearLayoutManager.setStackFromEnd(true);
        mChatMsgRV.setLayoutManager(linearLayoutManager);

    }
    private void loadChatMsgArrayCollection() {
        Query first = userChatMsgRef.orderBy("sentTime", Query.Direction.DESCENDING)
                .limit(15);
        first.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        mChatsMsgList.clear();
                        if (e != null) {
                            return;
                        }
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            BatiChatMsgModel chatMsgModel = documentSnapshot.toObject(BatiChatMsgModel.class);

                            mChatsMsgList.add(new BatiChatMsgModel(
                                    documentSnapshot.getId(),
                                    chatMsgModel.getMessage(),
                                    chatMsgModel.getSentBy(),
                                    chatMsgModel.getSentTime()
                            ));
                        }
                        if(queryDocumentSnapshots.size() <=15){
                            loading=true;
                            loadScrollViewRV(first);
                        }
                        mUserChatsAdapter.notifyDataSetChanged();
                    }
                });
    }

//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(getApplicationContext(), FireStoreUserActivity.class));
//
//    }
}