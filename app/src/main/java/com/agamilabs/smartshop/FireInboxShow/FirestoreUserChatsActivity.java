package com.agamilabs.smartshop.FireInboxShow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.controller.AppImageLoader;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreUserChatsActivity extends AppCompatActivity implements OnIntentUrl {

    String chatId, chatName;
    private CollectionReference userChatMsgRef ;
    private DocumentReference userChatDoc, userChatUnseenMsgDoc ;

    private RelativeLayout mRecyclerRelative,mChatMsgRelative;
    private ProgressBar mChatProgressbar ;
    private RecyclerView mChatMsgRV, mSelectImageRV ;
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

    //   for image
    private Uri imageuri;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();

    byte[] mThumbByteData, mRealByteData;
    private ArrayList<byte[]> mThumbListByte = new ArrayList<byte[]>();
    private ArrayList<byte[]> mRealListByte = new ArrayList<byte[]>();

    private StorageReference postStorageRef ;
    private List<String> getThumbUrl = new ArrayList<String>();
    private List<String> getReallUrl = new ArrayList<String>();

    //FrameLayout
    FrameLayout mFrameLayout;
    ImageView mFrameImageView;
    ImageButton mFrameImgBtn ;

//TODO:: onCreate
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

        postStorageRef = FirebaseStorage.getInstance().getReference();
        userChatMsgRef = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("chatMessages").collection(chatId);
        userChatDoc = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("chats").collection("chats").document(chatId);
        userChatUnseenMsgDoc = FirebaseFirestore.getInstance().collection("batikrom-message-collection").document("userChats");

       Initialize() ;

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        initializeAdapter();
        loadChatMsgArrayCollection() ;

        convertUnseenMsgByZero();


    }

    private void Initialize() {
        mChatMsgRV = findViewById(R.id.recycler_chatmsg) ;
        mChatMsgET = findViewById(R.id.edit_msgtext) ;
        mChatProgressbar = findViewById(R.id.progress_chat_firestore) ;
        mSelectImageRV = findViewById(R.id.recycler_select_image) ;
        mRecyclerRelative = findViewById(R.id.relative_recycler) ;
        mChatMsgRelative = findViewById(R.id.relative_chat_msg) ;
        mFrameLayout = findViewById(R.id.frame_layout) ;
        mFrameImageView = findViewById(R.id.frame_img_view) ;
        mFrameImgBtn = findViewById(R.id.frame_img_btn) ;
    }

    //TODO:: onSelectImageClick
    public void onSelectImageClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 1);



    }



    

    private void selectImageShowRV(int count) {
        if(count>0){
            mSelectImageRV.setVisibility(View.VISIBLE);
            mChatMsgRelative.getLayoutParams().height = 300;
//            Toast.makeText(this, " height: "+ 200, Toast.LENGTH_SHORT).show();
        }else{
            mSelectImageRV.setVisibility(View.GONE);
            mChatMsgRelative.getLayoutParams().height = 200;
//            Toast.makeText(this, " height: "+ 100, Toast.LENGTH_SHORT).show();

        }
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
        mUserChatsAdapter = new FireStoreUserChatsAdapter(getApplicationContext(), mChatsMsgList, this);
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
//                            BatiChatMsgModel chatMsgModel = documentSnapshot.toObject(BatiChatMsgModel.class);
//                            List<String> imageList = (List<String>) documentSnapshot.get("imageList");
//                            Log.e("imageList", imageList.toString()+"  "+ imageList.size() );

//                            mChatsMsgList.add(new BatiChatMsgModel(
//                                    documentSnapshot.getId(),
//                                    chatMsgModel.getMessage(),
//                                    chatMsgModel.getSentBy(),
//                                    chatMsgModel.getSentTime(),
//                                    imageList
//                            ));


                            HashMap<String, Object> imageMapList = (HashMap<String, Object>) documentSnapshot.get("imageList");
                            List<String> imageRealList= new ArrayList<>();
                            List<String> imageThumbList= new ArrayList<>();
                            if(imageMapList!=null){
                                Log.e("imageMapList", "real array:"+ imageMapList.get("real").toString() );
                                Log.e("imageMapList", "thub array:"+ imageMapList.get("thumb")  );
                                imageRealList = (List<String>) imageMapList.get("real") ;
                                imageThumbList = (List<String>) imageMapList.get("thumb") ;
                            }
                            Object sentTime =  documentSnapshot.get("sentTime") ;
                            mChatsMsgList.add(new BatiChatMsgModel(
                                    documentSnapshot.getId(),
                                    documentSnapshot.get("message").toString(),
                                    documentSnapshot.get("sentBy").toString(),
                                    sentTime,
                                    imageRealList,
                                    imageThumbList
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

    //TODO:: onSentMsgClick
    public void onSentMsgClick(View view) {
        adminId = sharedPreferences.getString(state, "");
        mSentMsg = mChatMsgET.getText().toString() ;

       getCurrentDateTime();

        Map<String, Object> chatMsgData = new HashMap<>();
        chatMsgData.put("message", mSentMsg);
        chatMsgData.put("sentBy", adminId);
        chatMsgData.put("sentTime", date);


//        if(ImageList.size()>0){
//            for (int upload_count = 0; upload_count < ImageList.size(); upload_count++) {
//                final Uri IndivitualImage = ImageList.get(upload_count);
//                final StorageReference thumbnilImage = postStorageRef.child("MsgThumbImage").child(adminId).child(IndivitualImage.getLastPathSegment()) ;
//
//                thumbnilImage.putBytes(ThumbListByte.get(upload_count))
//                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                thumbnilImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        String url = String.valueOf(uri);
//                                        thumbUrl.add(url);
//                                        Log.e("thumb_url", "thumb url sizes:   "+ thumbUrl.size()  ) ;
//
//                                        if( thumbUrl.size() == ThumbListByte.size() ){
//                                            chatMsgData.put("imageList", thumbUrl) ;
//
//
//                                                userChatMsgRef.document()
//                                                        .set(chatMsgData)
//                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                            @Override
//                                                            public void onSuccess(Void aVoid) {
////                                                                Toast.makeText(FirestoreUserChatsActivity.this, "successful...", Toast.LENGTH_SHORT).show();
//                                                                thumbUrl.clear();
//                                                                ImageList.clear();
//                                                                selectImageShowRV(thumbUrl.size());
//                                                                mChatMsgET.setText("");
//                                                                loadChatMsgArrayCollection();
//                                                            }
//                                                        })
//                                                        .addOnFailureListener(new OnFailureListener() {
//                                                            @Override
//                                                            public void onFailure(@NonNull Exception e) {
//                                                            }
//                                                        });
//                                        }
//
//
//
//
//                                    }
//                                }) ;
//                            }
//                        }) ;
//
//            }
//        }
//        else{
//            if(!mSentMsg.equalsIgnoreCase("")){
//                userChatMsgRef.document()
//                        .set(chatMsgData)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
////                                Toast.makeText(FirestoreUserChatsActivity.this, "successful...", Toast.LENGTH_SHORT).show();
//                                thumbUrl.clear();
//                                ImageList.clear();
//                                selectImageShowRV(thumbUrl.size());
//                                mChatMsgET.setText("");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                            }
//                        });
//            }
//        }



        if(ImageList.size()>0){
            for (int upload_count = 0; upload_count < ImageList.size(); upload_count++) {
                final Uri IndivitualImage = ImageList.get(upload_count);
                final StorageReference realImage = postStorageRef.child("RealImageFolder").child(adminId).child(IndivitualImage.getLastPathSegment()) ;
                final StorageReference thumbnilImage = postStorageRef.child("ThumbonilImageFolder").child(adminId).child(IndivitualImage.getLastPathSegment()) ;

                Map<String, Object> imageListMap = new HashMap<>();
                int finalUpload_count = upload_count;
                realImage.putBytes(mRealListByte.get(upload_count))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                realImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = String.valueOf(uri);
                                        getReallUrl.add(url);
//                                        if( getReallUrl.size() == ImageList.size()){
//                                            imageListMap.put("real", url);
//                                        }

                                        thumbnilImage.putBytes(mThumbListByte.get(finalUpload_count))
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        thumbnilImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                String url = String.valueOf(uri);
                                                                getThumbUrl.add(url);
                                                                if( getThumbUrl.size() == ImageList.size()  ){
                                                                    imageListMap.put("real", getReallUrl);
                                                                    imageListMap.put("thumb", getThumbUrl);

                                                                    chatMsgData.put("imageList", imageListMap) ;


                                                                    userChatMsgRef.document()
                                                                            .set(chatMsgData)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    getThumbUrl.clear();
                                                                                    getReallUrl.clear();
                                                                                    ImageList.clear();
                                                                                    selectImageShowRV(getReallUrl.size());
                                                                                    mChatMsgET.setText("");
                                                                                    loadChatMsgArrayCollection();
                                                                                    Toast.makeText(FirestoreUserChatsActivity.this, "success sent image", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                }
                                                                            });
                                                                }




                                                            }
                                                        });
                                                    }
                                                });

                                    }
                                }) ;
                            }
                        }) ;




            }
        }
        else{
            if(!mSentMsg.equalsIgnoreCase("")){
                userChatMsgRef.document()
                        .set(chatMsgData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(FirestoreUserChatsActivity.this, "successful...", Toast.LENGTH_SHORT).show();
                                getThumbUrl.clear();
                                ImageList.clear();
                                selectImageShowRV(getThumbUrl.size());
                                mChatMsgET.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }
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















    //TODO::: onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ImageList.clear();
                mRealListByte.clear();
                mThumbListByte.clear();

                if(data.getData()!=null){

                    Uri mImageUri=data.getData();
                    Log.e("bitmapSrc", "mImageUri" + mImageUri );


                    ImageList.add(mImageUri);

                    Bitmap bitmapSrc = null;
                    try{
                        bitmapSrc = MediaStore.Images.Media.getBitmap(FirestoreUserChatsActivity.this.getContentResolver(), mImageUri) ;
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.e("bitmapSrc", "bitmapSrc: "+ bitmapSrc ) ;

                    //===============   Real size image
                    Bitmap RealSizeBitmap = ImageReSizer.reduceBitmapSize(bitmapSrc, 360000) ;
                    Log.e("bitmapSrc", " fullSizeBitmap: "+ RealSizeBitmap ) ;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    RealSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    mRealByteData = baos.toByteArray();
                    Log.e("bitmapSrc", " thumbData: "+ mRealByteData) ;
                    mRealListByte.add(mRealByteData);


                    //========  Thumbonil Image
                    Bitmap thumbSizeBitmap = ImageReSizer.generateThumb(bitmapSrc, 6500) ;
                    Log.e("bitmapSrc", " thumbImgBitmap: "+ thumbSizeBitmap ) ;
                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                    thumbSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
                    mThumbByteData = baos2.toByteArray();
                    Log.e("bitmapSrc", " ImgthumbData: "+ mThumbByteData) ;
                    mThumbListByte.add(mThumbByteData);

                }
                else{
                    if (data.getClipData() != null) {


                        int count = data.getClipData().getItemCount();
                        int CurrentImageSelect = 0;
                        while (CurrentImageSelect < count) {
                            imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
//                        Picasso.get().load(imageuri).resize(200, 200).
//                                centerCrop().into(postImage);
                            ImageList.add(imageuri);
                            Log.e("bitmapSrc", "mImageUri" + imageuri );

                            Bitmap bitmapSrc = null;
                            try{
                                bitmapSrc = MediaStore.Images.Media.getBitmap(FirestoreUserChatsActivity.this.getContentResolver(), imageuri) ;
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            Log.e("bitmapSrc", "bitmapSrc else: "+ bitmapSrc ) ;

                            //===============   Real size image
                            Bitmap RealSizeBitmap = ImageReSizer.reduceBitmapSize(bitmapSrc, 360000) ;
                            Log.e("bitmapSrc", " fullSizeBitmap: "+ RealSizeBitmap ) ;
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            RealSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            mRealByteData = baos.toByteArray();
                            Log.e("bitmapSrc", " thumbData: "+ mRealByteData) ;
                            mRealListByte.add(mRealByteData);



                            //========  Thumbonil Image
                            Bitmap thumbSizeBitmap = ImageReSizer.generateThumb(bitmapSrc, 6500) ;
                            Log.e("bitmapSrc", " thumbImgBitmap: "+ thumbSizeBitmap ) ;
                            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                            thumbSizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
                            mThumbByteData = baos2.toByteArray();
                            Log.e("bitmapSrc", " ImgthumbData: "+ mThumbByteData) ;
                            mThumbListByte.add(mThumbByteData);

                            CurrentImageSelect = CurrentImageSelect + 1;
                        }

                        selectImageShowRV(ImageList.size());
                        List<String> selectImages = new ArrayList<>() ;
                        for(int i=0; i<ImageList.size(); i++){
                            selectImages.add(selectImages.size(), String.valueOf(ImageList.get(i)));
                        }


                        NestedFirestoreUserChatsAdapter productAdapter = new NestedFirestoreUserChatsAdapter(getApplicationContext(), selectImages,selectImages, this);
                        mSelectImageRV.setAdapter(productAdapter);
                        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.HORIZONTAL, false);
                        mSelectImageRV.setLayoutManager(manager);
                    }
                }


                Log.e("bitmapSrc", "ImageList: "+ ImageList.size() ) ;


            }

        }

//        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//        }
    }

    @Override
    public void onIntentUrl(String URL) {
        Log.e("onIntentUrl", "onIntentUrl:"+ URL  ) ;
        mFrameLayout.setVisibility(View.VISIBLE);
        mRecyclerRelative.setAlpha((float) 0.1);
        AppImageLoader.loadImageInView(URL, R.drawable.profile_image, (ImageView) mFrameImageView);

    }

    public void onClearFrameClick(View view) {
        mFrameLayout.setVisibility(View.GONE);
        mRecyclerRelative.setAlpha((float) 1.0);
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

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(getApplicationContext(), FireStoreUserActivity.class));
//
//    }



    }