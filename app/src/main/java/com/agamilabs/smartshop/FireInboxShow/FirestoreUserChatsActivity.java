package com.agamilabs.smartshop.FireInboxShow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FirestoreUserChatsActivity extends AppCompatActivity {

    String chatId;
    private CollectionReference userChatMsgRef ;
    private RecyclerView mChatMsgRV ;
    protected FireStoreUserChatsAdapter mUserChatsAdapter ;
    private List<BatiChatMsgModel> mChatsMsgList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestore_user_chats);

        chatId =  getIntent().getStringExtra("chatID");
        userChatMsgRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("chatMessages").collection(chatId);

        mChatMsgRV = findViewById(R.id.recycler_chatmsg) ;

        initializeAdapter();
        loadChatMsgArrayCollection() ;
    }

    private void initializeAdapter() {
//        AppController.getAppController().getInAppNotifier().log("adapter", "mBatiChatsList: "+ mBatiChatsList+"   mBatiUserChatsList:  "+mBatiUserChatsList );
        mUserChatsAdapter = new FireStoreUserChatsAdapter(getApplicationContext(), mChatsMsgList);
        mChatMsgRV.setAdapter(mUserChatsAdapter);
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        mChatMsgRV.setLayoutManager(manager);
    }
    private void loadChatMsgArrayCollection() {
        userChatMsgRef.orderBy("sentTime", Query.Direction.DESCENDING)
//                .limit(5)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
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
                        mUserChatsAdapter.notifyDataSetChanged();
                        AppController.getAppController().getInAppNotifier().log("chatmsg", mChatsMsgList.toString()    );
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), FireStoreUserActivity.class));

    }
}