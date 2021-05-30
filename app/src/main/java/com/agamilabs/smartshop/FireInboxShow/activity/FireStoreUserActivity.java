package com.agamilabs.smartshop.FireInboxShow.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agamilabs.smartshop.FireInboxShow.BatiChatMsgModel;
import com.agamilabs.smartshop.FireInboxShow.BatiChatsModal;
import com.agamilabs.smartshop.FireInboxShow.BatiUserChatsModal;
import com.agamilabs.smartshop.FireInboxShow.BatiUsersDetailsModal;
import com.agamilabs.smartshop.FireInboxShow.FireStoreUserAdapter;
import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.controller.AppImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FireStoreUserActivity extends AppCompatActivity {

    ListenerRegistration mBatiUserChatsListener, mBatiUserListener, mBatiChatsListener, mDocDetailsNameListener ;


    private TextView mAppbarTV ;
    private CircleImageView mAppbarImage ;
    private ImageButton mSearchImgBtn, mCancelImgBtn ;
    private EditText mSearchET ;
    private LinearLayout mSearchEditLinear ;
    private ProgressBar mUserProgressbar ;

    private RecyclerView mUserChatMsgRecyclerview;
    private FireStoreUserAdapter mBatiUserAdapter;
    private LinearLayoutManager linearLayoutManager ;
    private CollectionReference userRef, userMsgRef, msgUserChatsRef, msgChatsRef ;
    private DocumentReference userChatMsgDocRef;


    //.collection("asabbir47@gmail.com"),        kobir_store_maafe419rw@batikrom.shop,   .collection("+8801722373161")
// .collection("alif-shop")  rashed_shop_7q6c630wrq@batikrom.shop   116056194772555530699
//    private String USER_ID = "kobir_store_maafe419rw@batikrom.shop";
    public static String USER_ID = "rashed_shop_7q6c630wrq@batikrom.shop" ;

    public static String mUserName;
    private String mUserEmail, mUserPhoto;
    private List<BatiUsersDetailsModal> mBatiUsersDetailsList;
    private List<BatiUserChatsModal> mBatiUserChatsList;
    private List<BatiChatsModal> mBatiChatsList;
    private List<BatiChatMsgModel> mChatsMsgList ;


    SharedPreferences sharedPreferences ;
    static String SHARED_PREFS = "admin_store";
    String ADMIN_USER_ID ;
    String state = "admin_user_id";

    //for pagination
    private int lastVisiblesItems, dataExistNum=0;
    private boolean loading = false ;
    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 2000 ;

    int itemCount = 0;

    //TODO:: onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_store_user);

        Initialize() ;
//        USER_ID = getIntent().getStringExtra("admin_user_id") ;
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sharedSaved(sharedPreferences, state, USER_ID) ;
        ADMIN_USER_ID = sharedPreferences.getString("admin_user_id", USER_ID);



        userRef = FirebaseFirestore.getInstance().collection("batikrom-users");
        userMsgRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection");
        msgUserChatsRef = userMsgRef.document("userChats").collection(USER_ID);
        msgChatsRef = userMsgRef.document("chats").collection("chats");
        userChatMsgDocRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("chatMessages");


        mBatiUsersDetailsList = new ArrayList<>() ;
        mBatiUserChatsList = new ArrayList<>() ;
        mBatiChatsList = new ArrayList<>() ;
        mChatsMsgList = new ArrayList<>();



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

    }
    private void loadBatikromUsers() {
        mBatiUserListener = userRef.document(USER_ID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> document = snapshot.getData();
                    AppController.getAppController().getInAppNotifier().log("checking", document + "");

                    mUserName = document.get("name") + "";
                    mUserEmail = document.get("email") + "";
                    mUserPhoto = document.get("photo") + "";

                    mAppbarTV.setText(mUserName);
                    AppImageLoader.loadImageInView(mUserPhoto, R.drawable.profile_image, (ImageView) mAppbarImage);
//                        loadBatiUserChatsCollection();
                } else {
//                    Log.e("TAG", "Current data: null");
                }
            }
        });
    }
    private void loadBatiUserChatsCollection() {
        Query firstQuery = msgUserChatsRef
                .orderBy("lastupdatetime", Query.Direction.DESCENDING)
                .limit(15);
        mBatiUserChatsListener = firstQuery
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
                            Map<String, Object> batiUserChatsData = documentSnapshot.getData();
                            Log.e("batiUserChatsData", "batiUserChatsData: " + batiUserChatsData);
//                            BatiUserChatsModal products = documentSnapshot.toObject(BatiUserChatsModal.class);

//                            mBatiUserChatsList.add(new BatiUserChatsModal(
//                                    documentSnapshot.getId(),
//                                    products.getLastupdatetime(),
//                                    products.getUnseen_message()
//                            ));

                            mBatiUserChatsList.add(new BatiUserChatsModal(
                                    documentSnapshot.getId(),
                                    batiUserChatsData.get("lastupdatetime"),
                                    Integer.parseInt(batiUserChatsData.get("unseen_message") + "")
                            ));


                        }
                        Log.e("user_size","mBatiUserChatsList size: "+ mBatiUserChatsList.size() ) ;

                        loadBatiChatsCollection(mBatiUserChatsList);
                        itemCount = itemCount + mBatiUserChatsList.size();
                        if (queryDocumentSnapshots.size() <= 15) {
                            loading = true;
                            loadScrollViewRV(firstQuery);
                        }

                    }
                });
    }
    private void loadBatiChatsCollection(List<BatiUserChatsModal> mBatiUserChatsList) {
        mBatiChatsList.clear();
        mBatiChatsListener = msgChatsRef
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        for (int i = 0; i < mBatiUserChatsList.size(); i++) {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                List<String> usersList = (List<String>) documentSnapshot.get("users");
                                if (mBatiUserChatsList.get(i).getDocumentId().equals(documentSnapshot.getId())) {
                                    AppController.getAppController().getInAppNotifier().log("checking", " id: " + documentSnapshot.getId());
                                    for (int j = 0; j < usersList.size(); j++) {
                                        if (!usersList.get(j).equalsIgnoreCase(USER_ID)) {
                                            mBatiChatsList.add(new BatiChatsModal(
                                                    documentSnapshot.getId(),
                                                    usersList
                                            ));
                                        }
                                    }
                                }

                            }
                        }
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

                    loadDocumentDetailsName(userChatId, userChatName);
                }
            }


        }


    }
    private void loadDocumentDetailsName(String userDocumentId, String userChatName) {
        String finalUserChatName = userChatName;
        mDocDetailsNameListener =  userRef.document(userChatName).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }

                if (document != null && document.exists()) {
                    mBatiUsersDetailsList.add(new BatiUsersDetailsModal(
                            userDocumentId,
                            document.get("name").toString(),
                            document.get("email").toString(),
                            document.get("photo").toString()
                    ));

                } else {
                    mBatiUsersDetailsList.add(new BatiUsersDetailsModal(
                            userDocumentId,
                            finalUserChatName,
                            "",
                            ""
                    ));
                }
                loadChatMsgDocRef(userDocumentId) ;

                if(itemCount == mBatiUsersDetailsList.size()){
                    loading= true;
                }
            }
        });
    }
    private void loadChatMsgDocRef(String documentId) {
        Query first = userChatMsgDocRef.collection(documentId).orderBy("sentTime", Query.Direction.DESCENDING)
                .limit(1);
        first.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    Object sentTime =  documentSnapshot.get("sentTime") ;
                    mChatsMsgList.add(new BatiChatMsgModel(
                            documentId,
                            documentSnapshot.getId(),
                            documentSnapshot.get("message").toString(),
                            documentSnapshot.get("sentBy").toString(),
                            sentTime
                    ));
                }

                if(mBatiUsersDetailsList.size() == mChatsMsgList.size() && mChatsMsgList.size() == mBatiUserChatsList.size()){
                    mBatiUserAdapter.notifyDataSetChanged();

                }
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

        }
        if(text.equalsIgnoreCase("")){
            mBatiUserAdapter.filterList(filteredList, false);
        }else{
            mBatiUserAdapter.filterList(filteredList, true);
        }

    }
    public static void sharedSaved(SharedPreferences sharedPreferences, String state, String memberState){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(state, memberState);
        editor.apply();
    }


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

//TODO:: onResume
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("state", "onResume    1") ;

        loadBatikromUsers();
        loadBatiUserChatsCollection();
        initializeAdapter() ;

    }


//TODO:: onPause
    @Override
    protected void onPause() {
        Log.e("state", "onPause    1") ;
        super.onPause();


        mBatiUserListener.remove();
        mBatiUserChatsListener.remove();
        mBatiChatsListener.remove();
        mDocDetailsNameListener.remove();

    }

    private void Initialize() {
        mAppbarTV = findViewById(R.id.appbar_text) ;
        mAppbarImage = findViewById(R.id.appbar_circle_image) ;
        mSearchImgBtn = findViewById(R.id.appbar_searchbtn) ;
        mSearchET = findViewById(R.id.appbar_search_edit) ;
        mCancelImgBtn = findViewById(R.id.appbar_cancelbtn) ;
        mSearchEditLinear = findViewById(R.id.appbar_linear_search) ;
        mUserProgressbar = findViewById(R.id.progress_user_firestore) ;
        mUserChatMsgRecyclerview = findViewById(R.id.user_chat_recyclerview) ;
    }
    private void initializeAdapter() {
        mBatiUserAdapter = new FireStoreUserAdapter(getApplicationContext(), mBatiUsersDetailsList, mChatsMsgList, mBatiUserChatsList);
        mUserChatMsgRecyclerview.setAdapter(mBatiUserAdapter);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext()) ;
        mUserChatMsgRecyclerview.setLayoutManager(linearLayoutManager);
    }


}