package com.agamilabs.smartshop.FireInboxShow;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agamilabs.smartshop.R;


public class FirestoreChatsImageFragment extends Fragment {


    RecyclerView mFirestoreFragmentRV;
    FirestoreChatsImageAdapter mFirestoreChatsImageAdapter ;

    public FirestoreChatsImageFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firestore_chats_image, container, false);

        Toolbar toolbar = view.findViewById(R.id.firestore_user_chats_appbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
//        ((FirestoreUserChatsActivity)getActivity()).getSupportActionBar().setTitle(chatName);

        InitializeFields(view);

        return view;
    }

    private void InitializeFields(View view) {
        mFirestoreFragmentRV = view.findViewById(R.id.recycler_firestore_fragment) ;
    }

//    private void initializeAdapter() {
//        mFirestoreFragmentRV.setHasFixedSize(true);
//        mFirestoreChatsImageAdapter = new FirestoreChatsImageAdapter(getContext(), mChatsMsgList, this);
//        mChatMsgRV.setAdapter(mUserChatsAdapter);
//        linearLayoutManager = new LinearLayoutManager(getApplicationContext()) ;
////        linearLayoutManager.setStackFromEnd(true);
//        mChatMsgRV.setLayoutManager(linearLayoutManager);
//    }
}