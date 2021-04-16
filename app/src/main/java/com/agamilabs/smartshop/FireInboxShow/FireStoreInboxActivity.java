package com.agamilabs.smartshop.FireInboxShow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.model.BatikromUserMsgModel;
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

public class FireStoreInboxActivity extends AppCompatActivity {

    private DocumentReference userRef ;
    private CollectionReference userMsgRef ;
    private TextView mTextView ;
    private RecyclerView mUserChatMsgRecyclerview;

    private String USER_ID = "116056194772555530699" ;
    private String DOCUMENT_ID = "7hUH4zrS9GzKnBgQTSqj" ;
    private String bati_name, bati_email, bati_photo ;
    private List<BatikromUserMsgModel> userMsgList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_store_inbox);

        mTextView = findViewById(R.id.textView) ;
        mUserChatMsgRecyclerview = findViewById(R.id.user_chat_recyclerview) ;
        userRef = FirebaseFirestore.getInstance().collection("batikrom-users").document(USER_ID);
        userMsgRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("userChats").collection(USER_ID);


        userMsgList = new ArrayList<>() ;
//        loadBatikromUsers();

//        loadBatikromMsgCollection() ;
        loadBatikromMsgCollection1() ;
        AppController.getAppController().getInAppNotifier().log("response", "userMsgList " );

    }

    private void loadBatikromMsgCollection1() {
        userMsgRef.orderBy("lastupdatetime", Query.Direction.DESCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                Log.e("CHECK", "queryDocumentSnapshots:  "+ queryDocumentSnapshots+ " e: "+e  ) ;
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    BatikromUserMsgModel products = documentSnapshot.toObject(BatikromUserMsgModel.class);

                    Log.e("CHECK", "documentSnapshot:  "+ documentSnapshot ) ;

                    String id = documentSnapshot.getId() ;
                    Log.d("TAG", "ID: "+id) ;
                    userMsgList.add(new BatikromUserMsgModel(
                            documentSnapshot.getId(),
                            products.getLastupdatetime(),
                            products.getUnseen_message()
                    ));

                }

                Log.e("response", "list: "+ userMsgList ) ;

                AppController.getAppController().getInAppNotifier().log("listInside", "userMsgList: "+ userMsgList );
                PostProductListAdapter adapter = new PostProductListAdapter(getApplicationContext(), userMsgList);
                mUserChatMsgRecyclerview.setAdapter(adapter);
                GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
                mUserChatMsgRecyclerview.setLayoutManager(manager);

            }
        });

        Log.e("response", "list outside: "+ userMsgList ) ;
    }


    private void loadBatikromMsgCollection() {
        userMsgRef.orderBy("lastupdatetime", Query.Direction.DESCENDING)
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

                                userMsgRef.document(documentID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            AppController.getAppController().getInAppNotifier().log("document", document+""   );
                                            if (document.exists()) {

                                                BatikromUserMsgModel userMsgModel = document.toObject(BatikromUserMsgModel.class) ;
                                                userMsgList.add(new BatikromUserMsgModel(
                                                        documentID,
                                                        userMsgModel.getLastupdatetime(),
                                                        userMsgModel.getUnseen_message()) ) ;

                                                Timestamp timestamp = (Timestamp) userMsgModel.getLastupdatetime();
                                                Date date = timestamp.toDate();
                                                CharSequence dateFormat2 = DateFormat.format("yyyy-MM-dd hh:mm:ss a", date);


                                                mTextView.setText("Time: " +dateFormat2 );
//                                                mTextView.setText(documentID +"\n"+ document.get("lastupdatetime").toString()+" \n  "+ document.get("unseen_message")+"\n"+ date ) ;



                                            } else {
                                                AppController.getAppController().getInAppNotifier().log("response", "No such document");
                                            }
                                        } else {
                                            AppController.getAppController().getInAppNotifier().log("response", "get failed with "+ task.getException());
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
                            AppController.getAppController().getInAppNotifier().log("task", "Error getting documents: "+task.getException());
                        }
                    }
                });
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
                        AppController.getAppController().getInAppNotifier().log("response", "DocumentSnapshot data: " + document.getData().toString()   );
                        mTextView.setText(bati_name+" " + bati_email +  "  " + bati_photo  );
                    } else {
                        AppController.getAppController().getInAppNotifier().log("response", "No such document");
                    }
                } else {
                    AppController.getAppController().getInAppNotifier().log("response", "get failed with "+ task.getException());
                }
            }
        });
    }
}