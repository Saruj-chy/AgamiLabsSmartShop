package com.agamilabs.smartshop.FireInboxShow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class FireStoreUserActivity extends AppCompatActivity {

    private TextView mAppbarTV ;
    private CircleImageView mAppbarImage ;
    private ImageButton mSearchImgBtn, mCancelImgBtn ;
    private EditText mSearchET ;
    private LinearLayout mSearchEditLinear ;
    private Toolbar toolbar ;
    private ProgressBar mUserProgressbar ;

    private RecyclerView mUserChatMsgRecyclerview;
    private CircleImageView mClientImage;
    private FireStoreUserAdapter mBatiUserAdapter;
    private LinearLayoutManager linearLayoutManager ;
//    private DocumentReference userRef ;
    private CollectionReference userRef, userMsgRef, msgUserChatsRef, msgChatsRef ;
    private DocumentReference userChatMsgDocRef;


    //.collection("asabbir47@gmail.com"),        kobir_store_maafe419rw@batikrom.shop,   .collection("+8801722373161")
// .collection("alif-shop")  rashed_shop_7q6c630wrq@batikrom.shop   116056194772555530699
//    private String USER_ID = "kobir_store_maafe419rw@batikrom.shop";
    public static String USER_ID = "rashed_shop_7q6c630wrq@batikrom.shop" ;

    private String bati_name, bati_email, bati_photo ;
    private List<BatiUsersDetailsModal> mBatiUsersDetailsList;
    private List<BatiUserChatsModal> mBatiUserChatsList;
    private List<BatiChatsModal> mBatiChatsList;
    private List<BatiChatMsgModel> mChatsMsgList ;


    SharedPreferences sharedPreferences ;
    static String SHARED_PREFS = "admin_store";
    String ADMIN_USER_ID ;

    //for pagination
    private int lastVisiblesItems, dataExistNum=0;
    private boolean loading = false ;
    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 2000 ;

    int itemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_store_user);

        //TODO:: OnCreate
        Initialize() ;
//        USER_ID = getIntent().getStringExtra("admin_user_id") ;
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sharedSaved(sharedPreferences, "admin_user_id", USER_ID) ;
        ADMIN_USER_ID = sharedPreferences.getString("admin_user_id", USER_ID);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Smart Shop Firestore");



        userRef = FirebaseFirestore.getInstance().collection("batikrom-users");
        userMsgRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection");
        msgUserChatsRef = userMsgRef.document("userChats").collection(USER_ID);
        msgChatsRef = userMsgRef.document("chats").collection("chats");
        userChatMsgDocRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("chatMessages");


        mBatiUsersDetailsList = new ArrayList<>() ;
        mBatiUserChatsList = new ArrayList<>() ;
        mBatiChatsList = new ArrayList<>() ;
        mChatsMsgList = new ArrayList<>();


        initializeAdapter() ;
        loadBatikromUsers();


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
        mUserProgressbar = findViewById(R.id.progress_user_firestore) ;
        toolbar = findViewById(R.id.firestore_toolbar);
        mUserChatMsgRecyclerview = findViewById(R.id.user_chat_recyclerview) ;
        mClientImage = findViewById(R.id.circle_image_client) ;
    }

    //TODO:: initializeAdapter
    private void initializeAdapter() {
        mBatiUserAdapter = new FireStoreUserAdapter(getApplicationContext(), mBatiUsersDetailsList, mChatsMsgList, mBatiUserChatsList);
        mUserChatMsgRecyclerview.setAdapter(mBatiUserAdapter);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext()) ;
        mUserChatMsgRecyclerview.setLayoutManager(linearLayoutManager);
    }

    private void loadBatikromUsers() {
        userRef.document(USER_ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    AppController.getAppController().getInAppNotifier().log("checking", document+""   );
                    if (document.exists()) {
                        bati_name = document.get("name")+"" ;
                        bati_email = document.get("email")+"" ;
                        bati_photo= document.get("photo")+"" ;

                        mAppbarTV.setText(bati_name);
//                        AppImageLoader.loadImageInView(bati_photo, R.drawable.profile_image, (ImageView)mClientImage);
                        AppImageLoader.loadImageInView(bati_photo, R.drawable.profile_image, (ImageView)mAppbarImage);
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
        //TODO::  ref1
        Query firstQuery = msgUserChatsRef
                .orderBy("lastupdatetime", Query.Direction.DESCENDING)
                .limit(15);
        firstQuery
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        mBatiUserChatsList.clear();
                        mBatiChatsList.clear();
                        mBatiUsersDetailsList.clear();
                        mChatsMsgList.clear();


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
                        itemCount = itemCount + mBatiUserChatsList.size() ;
                        if(queryDocumentSnapshots.size() <=15){
                            loading=true;
                            loadScrollViewRV(firstQuery) ;
                        }

                    }
                });
    }

    private void loadBatiChatsCollection(List<BatiUserChatsModal> mBatiUserChatsList) {
        mBatiChatsList.clear();
        msgChatsRef
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        for( int i=0; i<mBatiUserChatsList.size(); i++ ){

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                List<String> usersList = (List<String>) documentSnapshot.get("users");
                                if(mBatiUserChatsList.get(i).getDocumentId().equals(documentSnapshot.getId()) ){
                                    AppController.getAppController().getInAppNotifier().log("checking"," id: "+ documentSnapshot.getId()   );
                                    for(int j=0; j<usersList.size(); j++){
                                        if(!usersList.get(j).equalsIgnoreCase(USER_ID)){
//                                            loadDocumentChatName(documentSnapshot.getId(), usersList.get(j));
                                            mBatiChatsList.add(new BatiChatsModal(
                                                    documentSnapshot.getId(),
                                                    usersList
                                            )) ;
                                        }
                                    }
                                }

                            }
                        }
//                        AppController.getAppController().getInAppNotifier().log("poking"," mBatiChatsList: "+ mBatiChatsList.size()   );
                        loadUserNamesCollection(mBatiChatsList);
                    }
                });

    }

    private void loadUserNamesCollection(List<BatiChatsModal> mBatiChatsList) {
        mBatiUsersDetailsList.clear();
        mChatsMsgList.clear();

        for(int i=0; i<mBatiChatsList.size(); i++ ){
            String userChatName = null;
            String userChatId = mBatiChatsList.get(i).getUserChatId() ;

            for(int j=0; j<mBatiChatsList.get(i).getUsersList().size(); j++){

                if(!mBatiChatsList.get(i).getUsersList().get(j).equalsIgnoreCase(USER_ID)){
                    userChatName = mBatiChatsList.get(i).getUsersList().get(j);
//                    AppController.getAppController().getInAppNotifier().log("checking", "userChatId in: "+ userChatId  );
                    
                    loadDocumentDetailsName(userChatId, userChatName);
                }
            }


        }


    }

    //TODO:: documentDetails
    private void loadDocumentDetailsName(String userChatId, String userChatName) {
        String finalUserChatName = userChatName;
        userRef.document(userChatName).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }

                if (document != null && document.exists()) {
                    mBatiUsersDetailsList.add(new BatiUsersDetailsModal(
                            userChatId,
                            document.get("name").toString(),
                            document.get("email").toString(),
                            document.get("photo").toString()
                    ));

                } else {
                    mBatiUsersDetailsList.add(new BatiUsersDetailsModal(
                            userChatId,
                            finalUserChatName,
                            "",
                            ""
                    ));
                }
                loadChatMsgDocRef(userChatId) ;

                if(itemCount == mBatiUsersDetailsList.size()){
                    loading= true;
                }

//                mBatiUserAdapter.notifyDataSetChanged();
            }
        });



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
    public static void sharedSaved(SharedPreferences sharedPreferences, String state, String memberState){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(state, memberState);
        editor.apply();
    }


    //TODO:: scroolRV
    private void loadScrollViewRV(Query next) {
        mUserChatMsgRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    lastVisiblesItems = linearLayoutManager.findLastVisibleItemPosition();

                    if(lastVisiblesItems >= itemCount-1 && loading){
                        loading=false;
                        mUserProgressbar.setVisibility(View.VISIBLE) ;

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
//                    Log.e("trick", "trick: "+ remainingRefreshTime) ;
                }

                @Override
                public void onFinish() {
//                    Log.e("trick", "finish: "+ remainingRefreshTime) ;
                    mUserProgressbar.setVisibility(View.GONE);
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

    //TODO::  Next
    private void loadNextFirestoreData(Query next) {
        next.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if(documentSnapshots.size()<=0){
                            return;
                        }
                        DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);

                        Query next =  msgUserChatsRef.orderBy("lastupdatetime", Query.Direction.DESCENDING)
                                .startAfter(lastVisible)
                                .limit(1);
                        next.get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot documentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {
                                            BatiUserChatsModal userChatsModel = documentSnapshot.toObject(BatiUserChatsModal.class);

                                            mBatiUserChatsList.add(new BatiUserChatsModal(
                                                    documentSnapshot.getId(),
                                                    userChatsModel.getLastupdatetime(),
                                                    userChatsModel.getUnseen_message()
                                            ));
                                        }
                                        loadBatiChatsCollection(mBatiUserChatsList) ;
                                        itemCount =  mBatiUserChatsList.size() ;
                                    }
                                });

                        loadScrollViewRV(next);
                    }
                });
    }



//TODO:: ChatMsgListMethod
    private void loadChatMsgDocRef(String chatId) {
        Query first = userChatMsgDocRef.collection(chatId).orderBy("sentTime", Query.Direction.DESCENDING)
                .limit(1);
        first.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
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


                Log.e("chat_msg", "mBatiUserChatsList:  "+ mBatiUserChatsList.size()  ) ;
                Log.e("chat_msg", "mBatiUsersDetailsList:  "+ mBatiUsersDetailsList.size()  ) ;
                Log.e("chat_msg", "mChatsMsgList:  "+ mChatsMsgList.size()  ) ;


                if(mBatiUsersDetailsList.size() == mChatsMsgList.size() && mChatsMsgList.size() == mBatiUserChatsList.size()){
//                    Log.e("chat_msg", "mBatiUserChatsList:  "+ mBatiUserChatsList.size()  ) ;
//                    Log.e("chat_msg", "mBatiUsersDetailsList:  "+ mBatiUsersDetailsList.size()  ) ;
//                    Log.e("chat_msg", "mChatsMsgList:  "+ mChatsMsgList.size()  ) ;
                    mBatiUserAdapter.notifyDataSetChanged();

                }
            }
        });
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
                        AppController.getAppController().getInAppNotifier().log("checking", "mBatiChatsList 1:  "+ mBatiChatsList );

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

        AppController.getAppController().getInAppNotifier().log("checking", "mBatiChatsList:  "+ mBatiChatsList );

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

                AppController.getAppController().getInAppNotifier().log("checking", "mBatiUserChatsList  1:  "+ mBatiUserChatsList );



//                AppController.getAppController().getInAppNotifier().log("listInside", "userMsgList: "+ userMsgList );
//                PostProductListAdapter adapter = new PostProductListAdapter(getApplicationContext(), userMsgList);
//                mUserChatMsgRecyclerview.setAdapter(adapter);
//                GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
//                mUserChatMsgRecyclerview.setLayoutManager(manager);

            }
        });

        AppController.getAppController().getInAppNotifier().log("checking", "mBatiUserChatsList:  "+ mBatiUserChatsList );
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