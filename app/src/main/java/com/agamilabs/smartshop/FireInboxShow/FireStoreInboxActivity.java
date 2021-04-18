package com.agamilabs.smartshop.FireInboxShow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FireStoreInboxActivity extends AppCompatActivity {

    private RecyclerView mUserChatMsgRecyclerview;
    private CircleImageView mClientImage;

    private BatikromUserAdapter mBatiUserAdapter;


    private DocumentReference userRef ;
    private CollectionReference userMsgRef, msgUserChatsRef, msgChatsRef ;

    private String USER_ID = "116056194772555530699" ;
    private String DOCUMENT_ID = "7hUH4zrS9GzKnBgQTSqj" ;
    private String bati_name, bati_email, bati_photo ;
    private List<BatiUserChatsModal> mBatiUserChatsList;
    private List<BatiChatsModal> mBatiChatsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_store_inbox);

        Toolbar toolbar = findViewById(R.id.firestore_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Smart Shop");

        mUserChatMsgRecyclerview = findViewById(R.id.user_chat_recyclerview) ;
        mClientImage = findViewById(R.id.circle_image_client) ;
        userRef = FirebaseFirestore.getInstance().collection("batikrom-users").document(USER_ID);
        userMsgRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection");
        msgUserChatsRef = userMsgRef.document("userChats").collection(USER_ID);
        msgChatsRef = userMsgRef.document("chats").collection("chats");


        mBatiUserChatsList = new ArrayList<>() ;
        mBatiChatsList = new ArrayList<>() ;


        loadBatikromUsers();
        initializeAdapter() ;









//        loadBatikromMsgCollection1() ;
//        AppController.getAppController().getInAppNotifier().log("response", "userMsgList " );
//
//        loadBatiMsgChatsCollection() ;
//        loadBatiMsgUserChatsCollection() ;

    }

    private void initializeAdapter() {
        AppController.getAppController().getInAppNotifier().log("adapter", "mBatiChatsList: "+ mBatiChatsList+"   mBatiUserChatsList:  "+mBatiUserChatsList );
        mBatiUserAdapter = new BatikromUserAdapter(getApplicationContext(), mBatiUserChatsList, mBatiChatsList);
        mUserChatMsgRecyclerview.setAdapter(mBatiUserAdapter);
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        mUserChatMsgRecyclerview.setLayoutManager(manager);
    }

    private void loadBatikromUsers() {

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    AppController.getAppController().getInAppNotifier().log("document", document+""   );
                    if (document.exists()) {
                        bati_name = document.get("name")+"" ;
                        bati_email = document.get("email")+"" ;
                        bati_photo= document.get("photo")+"" ;

                        AppImageLoader.loadImageInView(bati_photo, R.drawable.profile_image, (ImageView)mClientImage);
//                        loadBatiMsgUserChatsCollection() ;
//                        loadBatiMsgChatsCollection() ;

                        loadBatiUserChatsCollection();

                        AppController.getAppController().getInAppNotifier().log("response", "mBatiUserChatsList: " + mBatiUserChatsList+"  mBatiChatsList: "+ mBatiChatsList    );
//                        mTextView.setText(bati_name+" " + bati_email +  "  " + bati_photo  );
                    } else {
//                        AppController.getAppController().getInAppNotifier().log("response", "No such document");
                    }
                } else {
//                    AppController.getAppController().getInAppNotifier().log("response", "get failed with "+ task.getException());
                }
            }
        });
    }

    private void loadBatiUserChatsCollection() {
        msgUserChatsRef.orderBy("lastupdatetime", Query.Direction.DESCENDING)
//                .limit(1)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            BatiUserChatsModal products = documentSnapshot.toObject(BatiUserChatsModal.class);

                            mBatiUserChatsList.add(new BatiUserChatsModal(
                                    documentSnapshot.getId(),
                                    products.getLastupdatetime(),
                                    products.getUnseen_message()
                            ));

                        }
                        loadBatiChatsCollection(mBatiUserChatsList) ;
                    }
                });
    }

    private void loadBatiChatsCollection(List<BatiUserChatsModal> mBatiUserChatsList) {
//        AppController.getAppController().getInAppNotifier().log("batiUserChats", "loadBatiChatsCollection: "+ mBatiUserChatsList );
        msgChatsRef
//                .limit(1)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        Log.e("CHECK", "queryDocumentSnapshots:  "+ queryDocumentSnapshots+ " e: "+e  ) ;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            List<String> usersList = (List<String>) documentSnapshot.get("users");

                           for (int i=0; i<mBatiUserChatsList.size(); i++){
                               if(mBatiUserChatsList.get(i).getDocumentId().equals(documentSnapshot.getId()) ){
                                   AppController.getAppController().getInAppNotifier().log("docID", "documentID: "+ documentSnapshot.getId() );
                                   mBatiChatsList.add(new BatiChatsModal(
                                           documentSnapshot.getId(),
                                           usersList
                                   )) ;
                               }
                           }


                        }


                        AppController.getAppController().getInAppNotifier().log("docSnap", "mBatiChatsList: "+ mBatiChatsList+"   mBatiUserChatsList:  "+mBatiUserChatsList );
//                        initializeAdapter();
                        mBatiUserAdapter.notifyDataSetChanged();


                    }
                });

    }


    private void loadBatiMsgChatsCollection() {
        msgChatsRef
//                .limit(1)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        Log.e("CHECK", "queryDocumentSnapshots:  "+ queryDocumentSnapshots+ " e: "+e  ) ;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            List<String> usersList = (List<String>) documentSnapshot.get("users");
                            mBatiChatsList.add(new BatiChatsModal(
                                    documentSnapshot.getId(),
                                    usersList
                            )) ;

                        }
                        AppController.getAppController().getInAppNotifier().log("response", "mBatiChatsList 1:  "+ mBatiChatsList );

//                        Log.e("response", "list: "+ userChatsList ) ;
//                        mUserChatMsgRecyclerview.setVisibility(View.GONE);
//                        mTextView.setVisibility(View.VISIBLE);
//                        mTextView.setText(userChatsList+"");

//                        PostProductListAdapter adapter = new PostProductListAdapter(getApplicationContext(), userChatsList);
//                        mUserChatMsgRecyclerview.setAdapter(adapter);
//                        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
//                        mUserChatMsgRecyclerview.setLayoutManager(manager);

                    }
                });

        AppController.getAppController().getInAppNotifier().log("response", "mBatiChatsList:  "+ mBatiChatsList );

    }
    private void loadBatiMsgUserChatsCollection() {


        msgUserChatsRef.orderBy("lastupdatetime", Query.Direction.DESCENDING)
//                .limit(1)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    BatiUserChatsModal products = documentSnapshot.toObject(BatiUserChatsModal.class);

                    mBatiUserChatsList.add(new BatiUserChatsModal(
                            documentSnapshot.getId(),
                            products.getLastupdatetime(),
                            products.getUnseen_message()
                    ));

                }

                AppController.getAppController().getInAppNotifier().log("response", "mBatiUserChatsList  1:  "+ mBatiUserChatsList );



//                AppController.getAppController().getInAppNotifier().log("listInside", "userMsgList: "+ userMsgList );
//                PostProductListAdapter adapter = new PostProductListAdapter(getApplicationContext(), userMsgList);
//                mUserChatMsgRecyclerview.setAdapter(adapter);
//                GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
//                mUserChatMsgRecyclerview.setLayoutManager(manager);

            }
        });

        AppController.getAppController().getInAppNotifier().log("response", "mBatiUserChatsList:  "+ mBatiUserChatsList );
//        Log.e("response", "list outside: "+ userMsgList ) ;
    }
    private void loadBatikromMsgCollection1() {
        msgUserChatsRef.orderBy("lastupdatetime", Query.Direction.DESCENDING)
//        userMsgRef.orderBy("timeStamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        AppController.getAppController().getInAppNotifier().log("task", task.getResult()+"");
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentID = document.getId() ;
                                AppController.getAppController().getInAppNotifier().log("documentID", documentID);

                                msgUserChatsRef.document(documentID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            AppController.getAppController().getInAppNotifier().log("document", document+""   );
                                            if (document.exists()) {

                                                BatiUserChatsModal userMsgModel = document.toObject(BatiUserChatsModal.class) ;
                                                mBatiUserChatsList.add(new BatiUserChatsModal(
                                                        documentID,
                                                        userMsgModel.getLastupdatetime(),
                                                        userMsgModel.getUnseen_message()) ) ;

                                                Timestamp timestamp = (Timestamp) userMsgModel.getLastupdatetime();
                                                Date date = timestamp.toDate();
                                                CharSequence dateFormat2 = DateFormat.format("yyyy-MM-dd hh:mm:ss a", date);


//                                                mTextView.setText("Time: " +dateFormat2 );
//                                                mTextView.setText(documentID +"\n"+ document.get("lastupdatetime").toString()+" \n  "+ document.get("unseen_message")+"\n"+ date ) ;



                                            } else {
//                                                AppController.getAppController().getInAppNotifier().log("response", "No such document");
                                            }
                                        } else {
//                                            AppController.getAppController().getInAppNotifier().log("response", "get failed with "+ task.getException());
                                        }
//                                        AppController.getAppController().getInAppNotifier().log("list", "userMsgList: "+ userMsgList );
//                                        AppController.getAppController().getInAppNotifier().log("listInside", "userMsgList: "+ userMsgList );
//                                        PostProductListAdapter adapter = new PostProductListAdapter(getApplicationContext(), userMsgList);
//                                        mUserChatMsgRecyclerview.setAdapter(adapter);
//                                        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
//                                        mUserChatMsgRecyclerview.setLayoutManager(manager);
                                    }

                                });


//                                AppController.getAppController().getInAppNotifier().log("listData", documentID +"\n"+ document.get("lastupdatetime").toString()+" \n  "+ document.get("unseen_message")+"\n" );

                            }



                        } else {
//                            AppController.getAppController().getInAppNotifier().log("task", "Error getting documents: "+task.getException());
                        }
                    }
                });
    }


}