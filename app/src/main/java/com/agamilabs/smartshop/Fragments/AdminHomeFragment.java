package com.agamilabs.smartshop.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.adapter.AllNotificationViewAdapter;
import com.agamilabs.smartshop.database.DatabaseHandler;
import com.agamilabs.smartshop.model.NotifyModel;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {
    private RecyclerView mRecyclerView ;
    private List<NotifyModel> mNotifyList;
    private ArrayList<NotifyModel> mSqLiteList;
    private DatabaseHandler mDbHandler;
    private AllNotificationViewAdapter mNotifyAdapter;
    private GridLayoutManager mGridManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_admin_home) ;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mNotifyList = new ArrayList<>() ;
        mDbHandler = new DatabaseHandler(getContext()) ;

        showDataAtListView();

        return view ;
    }

    public void showDataAtListView() {
        mSqLiteList = mDbHandler.getAllInfo(getContext());
//        Toast.makeText(this, "Yes", Toast.LENGTH_SHORT).show();

        Log.e("TAG", "size:  "+ mSqLiteList.size() ) ;

        for (int i = 0; i < mSqLiteList.size(); i++) {

            if (!mSqLiteList.get(i).getTopic().isEmpty())
            {
                mNotifyList.add(
                        new NotifyModel(
                                mSqLiteList.get(i).getId(),
                                mSqLiteList.get(i).getTitle(),
                                mSqLiteList.get(i).getBody_text(),
                                mSqLiteList.get(i).getTopic()
                        )
                );
            }


        }
        Log.d("TAG", "tempList: "+ mNotifyList) ;

        mNotifyAdapter = new AllNotificationViewAdapter(getContext(), mNotifyList);
        mRecyclerView.setAdapter(mNotifyAdapter);
        mGridManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mGridManager);

    }
}