package com.agamilabs.smartshop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.adapter.AllNotificationViewAdapter;
import com.agamilabs.smartshop.adapter.DashboardAdapter;
import com.agamilabs.smartshop.adapter.SmothPagerAdapter;
import com.agamilabs.smartshop.controller.AppController;
import com.agamilabs.smartshop.database.DatabaseHandler;
import com.agamilabs.smartshop.model.NotifyModel;
import com.agamilabs.smartshop.model.SmothPagerModel;
import com.airbnb.lottie.LottieAnimationView;
import com.astritveliu.boom.Boom;
import com.av.smoothviewpager.Smoolider.SmoothViewpager;

import java.util.ArrayList;
import java.util.List;

public class SmothViewPagerActivity extends AppCompatActivity {
    //smothpager
    private TextView mSmothTitle, mSmothDetails;
    private SmoothViewpager mSmothPager;
    private LottieAnimationView mLottieAnimation;

    private final int[] pics = {R.drawable.smart_shop, R.drawable.smart_shop_logo, R.drawable.smart_shop_cream, R.drawable.shoe, R.drawable.smart_shop};
    private final String[] titles = {"Stock Report", "Order Report", "Sale Invoices", "Campaign", "Recharge"};
    private final int[] descriptions = {R.string.description_1, R.string.description_2, R.string.description_3, R.string.description_4, R.string.description_5};
    private List<SmothPagerModel> mItemList;


    //====   smart shop next feature
    private RecyclerView mPosRecycler, mECommerceRecycler, mPromotionRecycler, mNotificationRecycler ;
    private DashboardAdapter  mDashboardAdapter ;
    private GridLayoutManager  mGridManager ;
    private List<SmothPagerModel> mPOSList = new ArrayList<>();
    private List<SmothPagerModel> mEcomList = new ArrayList<>();
    private List<SmothPagerModel> mProList = new ArrayList<>();


    //   notification component
    private List<NotifyModel> mNotifyList = new ArrayList<>();
    private ArrayList<NotifyModel> mSqLiteList;
    private DatabaseHandler mDbHandler;
    private AllNotificationViewAdapter mNotifyAdapter;

    //=============  timer
    int currentPage = 0;
    private CountDownTimer countDownTimer;
    long remainingRefreshTime = 60*60*1000 ;
    boolean action = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoth_view_pager);

        Initialize() ;

        produceAdapterList(mDashboardAdapter, mGridManager,  mPosRecycler, R.array.pos_array, pics, mPOSList);
        produceAdapterList(mDashboardAdapter, mGridManager, mECommerceRecycler, R.array.ecommerce_array, pics, mEcomList);
        produceAdapterList(mDashboardAdapter, mGridManager, mPromotionRecycler, R.array.promotion_array, pics, mProList);

        mNotificationRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mNotifyAdapter = new AllNotificationViewAdapter(getApplicationContext(), mNotifyList);
        mNotificationRecycler.setAdapter(mNotifyAdapter);
        mDbHandler = new DatabaseHandler(getApplicationContext());
        showDataAtListView();



        generatePagerItemList();
        mSmothPager.setAdapter( new SmothPagerAdapter(mItemList,getApplicationContext()));
        mSmothPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {

            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                AppController.getAppController().getInAppNotifier().log("TAG", "position scrolled:  "+ position  );

            }

            public void onPageSelected(final int position) {
                // handle operations with current page
                swipeTextDetails(position);
                currentPage=position ;
//                AppController.getAppController().getInAppNotifier().log("TAG", "position selected:  "+ position  );

            }
        });
        mSmothTitle.setText(titles[0]);
        mSmothDetails.setText(descriptions[0]);
        new Boom(mSmothTitle);
        new Boom(mSmothDetails);

        setAutoRefresh();


    }

    private List<SmothPagerModel> produceArrayList(String[] titles, int[] pics) {
        List<SmothPagerModel> arrayList = new ArrayList<>();
        for (int i=0; i<titles.length; i++){
            arrayList.add(
                    new SmothPagerModel(
                            titles[i],
                            pics[i]
                    )
            );
        }

        AppController.getAppController().getInAppNotifier().log("list", "  mItemList:  "+ mItemList);

        return arrayList ;

    }


    private void produceAdapterList(DashboardAdapter adapter, GridLayoutManager manager, RecyclerView recycler,  int array, int[] pics,
                                    List<SmothPagerModel> arrayList) {
       arrayList = produceArrayList(getResources().getStringArray(array), pics) ;

        adapter = new DashboardAdapter(this, arrayList);
        manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(manager);
    }
    public void showDataAtListView() {
        mSqLiteList = mDbHandler.getAllInfo(getApplicationContext());
        mNotifyList.clear();
        mNotifyList.addAll(mSqLiteList);
        mNotifyAdapter.notifyDataSetChanged();
    }
    private void Initialize() {
        mSmothTitle = findViewById(R.id.text_smoth_title) ;
        mSmothDetails = findViewById(R.id.text_smoth_details) ;
        mSmothPager = findViewById(R.id.smoth_viewpager) ;
        mLottieAnimation = findViewById(R.id.lottie_animation) ;

        mPosRecycler = findViewById(R.id.recycler_pos) ;
        mECommerceRecycler = findViewById(R.id.recycler_ecommerce) ;
        mPromotionRecycler = findViewById(R.id.recycler_promotion) ;
        mNotificationRecycler = findViewById(R.id.recycler_notification) ;
    }
    private void generatePagerItemList(){
        mItemList = new ArrayList<>();
        for(int i=0;i<pics.length;i++){
            SmothPagerModel gift = new SmothPagerModel();
            gift.setImageUrl(pics[i]);
            gift.setName(titles[i]);
            mItemList.add(gift);
        }
    }
    private void swipeTextDetails(int pos) {

        mSmothTitle.setVisibility(View.INVISIBLE);
        mSmothTitle.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
        mSmothTitle.setVisibility(View.VISIBLE);
        mSmothTitle.setText(titles[pos % titles.length]);

        mSmothDetails.setVisibility(View.INVISIBLE);
        mSmothDetails.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
        mSmothDetails.setVisibility(View.VISIBLE);
        mSmothDetails.setText(descriptions[pos % descriptions.length]);
    }
    private void setAutoRefresh(){

        mLottieAnimation.playAnimation();
        if(remainingRefreshTime<=0){
            if(countDownTimer!= null){
                countDownTimer.cancel();
            }
            return;

        }
        if(countDownTimer == null){
            countDownTimer = new CountDownTimer(remainingRefreshTime, 10*1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (currentPage == mItemList.size()-1) {
                        action=false ;
                    }else if(currentPage == 0){
                        action=true ;
                    }
                    if(action){
                        mSmothPager.setCurrentItem(currentPage++, true);
                    }else{
                        mSmothPager.setCurrentItem(currentPage--, true);
                    }

                }

                @Override
                public void onFinish() {
                    AppController.getAppController().getInAppNotifier().log("TAG", "onFinish");
                }
            };

            countDownTimer.start() ;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
//        countDownTimer.cancel();
    }
    @Override
    protected void onStart() {
        super.onStart();
        countDownTimer=null ;
    }
}