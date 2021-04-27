package com.agamilabs.smartshop.FireInboxShow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

    private NestedScrollView mNestedScroll ;
    private RecyclerView mChatMsgRV ;
    private LinearLayoutManager linearLayoutManager;
    protected FireStoreUserChatsAdapter mUserChatsAdapter ;
    private List<BatiChatMsgModel> mChatsMsgList = new ArrayList<>();
    private int totalItemCount, pastVisiblesItems,  visibleItemCount, page =1, previousTotal ;
    private boolean loading = true ;


    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestore_user_chats);

        chatId =  getIntent().getStringExtra("chatID");
        userChatMsgRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("chatMessages").collection(chatId);

        mChatMsgRV = findViewById(R.id.recycler_chatmsg) ;


        mNestedScroll = findViewById(R.id.nested_scroll_chatmsg) ;

        initializeAdapter();
        loadChatMsgArrayCollection() ;


//        loadNextFirestoreData();



    }

    private void loadScrollViewRV(Query first) {
        Log.e("notify", "notify: " ) ;

        mChatMsgRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("RV", "dx: "+dx+"  dy:  "+dy ) ;
                if(dy<0){
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                    previousTotal = linearLayoutManager.findLastCompletelyVisibleItemPosition();


                   if(pastVisiblesItems == 0 && loading){
                           loading=false;
                           loadNextFirestoreData(first);
//                           Toast.makeText(FirestoreUserChatsActivity.this, "length: "+ mChatsMsgList.size(), Toast.LENGTH_SHORT).show();
                   }
                }

            }
        });
    }



    private void loadNextFirestoreData(Query first){
        first.get()
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

                                            mChatsMsgList.add(new BatiChatMsgModel(
                                                    documentSnapshot.getId(),
                                                    chatMsgModel.getMessage(),
                                                    chatMsgModel.getSentBy(),
                                                    chatMsgModel.getSentTime()
                                            ));
                                        }
                                        mUserChatsAdapter.notifyDataSetChanged();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), FireStoreUserActivity.class));

    }
}