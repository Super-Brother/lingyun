package com.gyzh.app.lingyun.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class MyFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> arrayFragmentlList;

    public MyFragmentAdapter(FragmentManager fragmentManager, ArrayList<Fragment> arrayFragmentlList) {
        super(fragmentManager);
        this.arrayFragmentlList = arrayFragmentlList;
    }

    @Override
    public Fragment getItem(int arg0) {
        if (arrayFragmentlList != null) {
            return arrayFragmentlList.get(arg0);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (arrayFragmentlList != null) {
            return arrayFragmentlList.size();
        }
        return 0;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

}
