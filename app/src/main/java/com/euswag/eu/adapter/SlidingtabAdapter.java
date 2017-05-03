package com.euswag.eu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ASUS on 2017/3/15.
 */

public class SlidingtabAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments;
    private String[] titles;

    public SlidingtabAdapter(FragmentManager fragmentManager, Fragment[] fragments,String[] titles){
        super(fragmentManager);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
