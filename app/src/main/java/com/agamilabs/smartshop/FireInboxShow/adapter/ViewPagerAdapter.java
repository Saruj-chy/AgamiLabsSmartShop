package com.agamilabs.smartshop.FireInboxShow.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.agamilabs.smartshop.FireInboxShow.FirestoreChatsImageFragment;
import com.agamilabs.smartshop.FireInboxShow.Interface.onZoomOutInterface;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    List<String> imageList = new ArrayList<>();

//    onZoomOutInterface onZoomOutInterface ;

    public ViewPagerAdapter(FragmentManager fragmentManager, List<String> imageList) {
        super(fragmentManager);
        this.imageList = imageList ;
//        this.onZoomOutInterface = onZoomOutInterface ;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return FirestoreChatsImageFragment.newInstance(position, imageList.get(position), 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}