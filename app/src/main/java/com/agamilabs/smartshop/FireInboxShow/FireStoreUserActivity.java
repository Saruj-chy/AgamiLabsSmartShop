package com.agamilabs.smartshop.FireInboxShow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
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

public class FireStoreUserActivity extends AppCompatActivity {

    private TextView mAppbarTV ;
    private CircleImageView mAppbarImage ;
    private ImageButton mSearchImgBtn, mCancelImgBtn ;
    private EditText mSearchET ;
    private LinearLayout mSearchEditLinear ;
    private Toolbar toolbar ;

    private RecyclerView mUserChatMsgRecyclerview;
    private CircleImageView mClientImage;

    private FireStoreUserAdapter mBatiUserAdapter;
//    private DocumentReference userRef ;
    private CollectionReference userRef, userMsgRef, msgUserChatsRef, msgChatsRef ;

//.collection("asabbir47@gmail.com"),        kobir_store_maafe419rw@batikrom.shop,   .collection("+8801722373161")
// .collection("alif-shop")  rashed_shop_7q6c630wrq@batikrom.shop   116056194772555530699
//    private String USER_ID = "kobir_store_maafe419rw@batikrom.shop";
    public static final String USER_ID = "rashed_shop_7q6c630wrq@batikrom.shop" ;

    private String bati_name, bati_email, bati_photo ;
    private List<BatiUsersDetailsModal> mBatiUsersDetailsList;
    private List<BatiUserChatsModal> mBatiUserChatsList;
    private List<BatiChatsModal> mBatiChatsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_store_user);

        Initialize() ;


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Smart Shop Firestore");



        userRef = FirebaseFirestore.getInstance().collection("batikrom-users");
        userMsgRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection");
        msgUserChatsRef = userMsgRef.document("userChats").collection(USER_ID);
        msgChatsRef = userMsgRef.document("chats").collection("chats");


        mBatiUsersDetailsList = new ArrayList<>() ;
        mBatiUserChatsList = new ArrayList<>() ;
        mBatiChatsList = new ArrayList<>() ;


        loadBatikromUsers();
        initializeAdapter() ;

        mSearchImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchEditLinear.setVisibility(View.VISIBLE);
                mSearchImgBtn.setVisibility(View.GONE);

                mSearchET.requestFocus() ;
            }
        });
        mCancelImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchET.setText("") ;
                mSearchEditLinear.setVisibility(View.GONE) ;
                mSearchImgBtn.setVisibility(View.VISIBLE) ;


            }
        });


        AddTextChange();





//        loadBatikromMsgCollection1() ;
//        AppController.getAppController().getInAppNotifier().log("response", "userMsgList " );
//
//        loadBatiMsgChatsCollection() ;
//        loadBatiMsgUserChatsCollection() ;

    }

    private void Initialize() {
        mAppbarTV = findViewById(R.id.appbar_text) ;
        mAppbarImage = findViewById(R.id.appbar_circle_image) ;
        mSearchImgBtn = findViewById(R.id.appbar_searchbtn) ;
        mSearchET = findViewById(R.id.appbar_search_edit) ;
        mCancelImgBtn = findViewById(R.id.appbar_cancelbtn) ;
        mSearchEditLinear = findViewById(R.id.appbar_linear_search) ;
        toolbar = findViewById(R.id.firestore_toolbar);
        mUserChatMsgRecyclerview = findViewById(R.id.user_chat_recyclerview) ;
        mClientImage = findViewById(R.id.circle_image_client) ;
    }

    private void initializeAdapter() {
        mBatiUserAdapter = new FireStoreUserAdapter(getApplicationContext(), mBatiUsersDetailsList);
        mUserChatMsgRecyclerview.setAdapter(mBatiUserAdapter);
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        mUserChatMsgRecyclerview.setLayoutManager(manager);
    }

    private void loadBatikromUsers() {
        Log.e("load", "loadBatikromUsers") ;

        userRef.document(USER_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    AppController.getAppController().getInAppNotifier().log("document", document+""   );
                    if (document.exists()) {
                        bati_name = document.get("name")+"" ;
                        bati_email = document.get("email")+"" ;
                        bati_photo= document.get("photo")+"" ;

                        mAppbarTV.setText(bati_name);
//                        AppImageLoader.loadImageInView(bati_photo, R.drawable.profile_image, (ImageView)mClientImage);
                        AppImageLoader.loadImageInView(bati_photo, R.drawable.profile_image, (ImageView)mAppbarImage);
//                        loadBatiMsgUserChatsCollection() ;
//                        loadBatiMsgChatsCollection() ;

                        loadBatiUserChatsCollection();
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
        Log.e("load", "loadBatiUserChatsCollection") ;

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
                        AppController.getAppController().getInAppNotifier().log("userChats", mBatiUserChatsList.toString()    );

//                        loadUserNamesCollection(mBatiUserChatsList);
                        loadBatiChatsCollection(mBatiUserChatsList) ;
                    }
                });
    }

    private void loadBatiChatsCollection(List<BatiUserChatsModal> mBatiUserChatsList) {
        Log.e("load", "loadBatiChatsCollection") ;

        msgChatsRef
//                .limit(1)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
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
                        AppController.getAppController().getInAppNotifier().log("chats", mBatiChatsList.toString()    );
                        loadUserNamesCollection(mBatiChatsList);
                    }
                });

    }

    private void loadUserNamesCollection(List<BatiChatsModal> mBatiChatsList) {
        Log.e("load", "loadUserNamesCollection") ;

        for(int i=0; i<mBatiChatsList.size(); i++){
            String userChatName = null;
            String userChatId = mBatiChatsList.get(i).getUserChatId() ;
            for(int j=0; j<mBatiChatsList.get(i).getUsersList().size(); j++){

                if(!mBatiChatsList.get(i).getUsersList().get(j).equalsIgnoreCase(USER_ID)){
                    userChatName = mBatiChatsList.get(i).getUsersList().get(j);
                }
            }
            Log.e("userChatName", "userChatName: "+ userChatName+" userChatId: "+userChatId );
            AppController.getAppController().getInAppNotifier().log("details_list", "mBatiUsersDetailsList out: "+ mBatiUsersDetailsList.size());

//            mBatiUsersDetailsList.clear();
            String finalUserChatName = userChatName;
            userRef.document(userChatName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            mBatiUsersDetailsList.add(new BatiUsersDetailsModal(
                                    userChatId,
                                    document.get("name").toString(),
                                    document.get("email").toString(),
                                    document.get("photo").toString()
                            ));

                            AppController.getAppController().getInAppNotifier().log("userChatName", "name: "+ document.get("name").toString());
                        } else {
                            mBatiUsersDetailsList.add(new BatiUsersDetailsModal(
                                    userChatId,
                                    finalUserChatName,
                                    "",
                                    ""
                            ));
                        AppController.getAppController().getInAppNotifier().log("userChatName", "userChatId: "+ userChatId);
                        }

                        mBatiUserAdapter.notifyDataSetChanged();
                    } else {
//                    AppController.getAppController().getInAppNotifier().log("response", "get failed with "+ task.getException());
                    }


                }
            });
            AppController.getAppController().getInAppNotifier().log("details_list", "mBatiUsersDetailsList in: "+ mBatiUsersDetailsList.size());


        }
        AppController.getAppController().getInAppNotifier().log("details_list", "mBatiUsersDetailsList: "+ mBatiUsersDetailsList.size());


    }

    public void AddTextChange(){
        mSearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });
    }
    private void filter(String text) {
        List<BatiUsersDetailsModal> filteredList = new ArrayList<>();

        for (BatiUsersDetailsModal item : mBatiUsersDetailsList) {

            if (item.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
//            if(String.valueOf(item.getPhone()).toLowerCase().contains(text.toLowerCase())){
//                filteredList.add(item);
//            }
        }
        mBatiUserAdapter.filterList(filteredList);
    }


























    //now not countable, but nessary
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