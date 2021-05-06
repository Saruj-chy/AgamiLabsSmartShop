package com.agamilabs.smartshop.FireInboxShow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirestoreUserChatsActivity extends AppCompatActivity {

    String chatId, chatName;
    private CollectionReference userChatMsgRef ;
    private DocumentReference userChatDoc, userChatUnseenMsgDoc ;

    private ProgressBar mChatProgressbar ;
    private RecyclerView mChatMsgRV ;
    private EditText mChatMsgET;
    private LinearLayoutManager linearLayoutManager;
    protected FireStoreUserChatsAdapter mUserChatsAdapter ;
    private List<BatiChatMsgModel> mChatsMsgList = new ArrayList<>();
    private int firstVisiblesItems, dataExistNum=0;
    private boolean loading = true ;


    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 2000 ;


    //==========  msg
    private String adminId, mSentMsg ;
    Date date = null;
    //=========   sharedprefarence
    SharedPreferences sharedPreferences;
    String SHARED_PREFS = "admin_store";
    String state = "admin_user_id";



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
        userChatDoc = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("chats").collection("chats").document(chatId);
        userChatUnseenMsgDoc = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("userChats");

        mChatMsgRV = findViewById(R.id.recycler_chatmsg) ;
        mChatMsgET = findViewById(R.id.edit_msgtext) ;
        mChatProgressbar = findViewById(R.id.progress_chat_firestore) ;

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        initializeAdapter();
        loadChatMsgArrayCollection() ;

        convertUnseenMsgByZero();


    }

    private void convertUnseenMsgByZero() {
        adminId = sharedPreferences.getString(state, "");
        getCurrentDateTime();

        Map<String, Object> chatUnseenMsgData = new HashMap<>();
        chatUnseenMsgData.put("lastupdatetime", date);
        chatUnseenMsgData.put("unseen_message", 0);

         DocumentReference unseenMsgDocument =userChatUnseenMsgDoc.collection(adminId).document(chatId);

        unseenMsgDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    int unseenCount = Integer.parseInt(documentSnapshot.get("unseen_message") + "");
                    if(unseenCount>0){
                        unseenMsgDocument.set(chatUnseenMsgData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                        Log.e("documentSnapshot", " onSuccess i: "+ unseenCount  ) ;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                Log.e("documentSnapshot", " onFailure: "+ unseenCount  ) ;
                            }
                        }) ;
                    }

                } else {
                    return;
                }
            }
        });



    }

    private void loadScrollViewRV(Query next) {
        mChatMsgRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy<0){
                    firstVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                   if(firstVisiblesItems == 0 && loading){
                       loading=false;
                       mChatProgressbar.setVisibility(View.VISIBLE);

                       setAutoRefresh(next);
                   }
                }

            }
        });
    }
    private void setAutoRefresh(Query next){
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
//                   Log.e("trick", "trick: "+ remainingRefreshTime) ;
                }

                @Override
                public void onFinish() {
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
                                        linearLayoutManager.scrollToPositionWithOffset(dataExistNum-1,  0);
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

    public void onSentMsgClick(View view) {
        adminId = sharedPreferences.getString(state, "");
        mSentMsg = mChatMsgET.getText().toString() ;

       getCurrentDateTime();


        Map<String, Object> chatMsgData = new HashMap<>();
        chatMsgData.put("message", mSentMsg);
        chatMsgData.put("sentBy", adminId);
        chatMsgData.put("sentTime", date);


        if(!mSentMsg.equalsIgnoreCase("")){
            userChatMsgRef.document()
                    .set(chatMsgData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mChatMsgET.setText("");
                            loadBatiChatsCollection();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(FirestoreUserChatsActivity.this, "failed: " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void getCurrentDateTime() {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String timeStamp = dateFormat1.format(new Date()) ;

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        try {
            date = dateFormat2.parse(timeStamp);
        } catch (ParseException e){
        }


    }

    private void loadBatiChatsCollection() {

        userChatDoc.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                List<String> usersList = (List<String>) documentSnapshot.get("users");
                List<String> userId = new ArrayList<>() ;
                AppController.getAppController().getInAppNotifier().log("checking", " id: " + documentSnapshot.getId());
                for (int j = 0; j < usersList.size(); j++) {
                    if(!usersList.get(j).equalsIgnoreCase(adminId)){
                        userId.add(usersList.get(j));
                    }
                }
                loadUserChatsCollection(userId);

            }
        });


    }

    private void loadUserChatsCollection(List<String> userIdList) {
        for(int i=0; i<userIdList.size(); i++ ) {
            String userId = userIdList.get(i);
            userChatUnseenMsgDoc.collection(userId).document(chatId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        int unseenCount = Integer.parseInt(documentSnapshot.get("unseen_message") + "");
                        updateUnseenMsgDocument(userId, unseenCount);
                    } else {
                        updateUnseenMsgDocument(userId, 0);
                    }
                }
            });
        }
    }

    private void updateUnseenMsgDocument(String userId,int unseenCount) {

        Map<String, Object> chatUnseenMsgData = new HashMap<>();
        chatUnseenMsgData.put("lastupdatetime", date);
        chatUnseenMsgData.put("unseen_message", unseenCount+1);
        userChatUnseenMsgDoc.collection(userId).document(chatId).set(chatUnseenMsgData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Log.e("documentSnapshot", " onSuccess i: "+ unseenCount  ) ;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Log.e("documentSnapshot", " onFailure: "+ unseenCount  ) ;
            }
        }) ;



    }



























//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(getApplicationContext(), FireStoreUserActivity.class));
//
//    }



    }