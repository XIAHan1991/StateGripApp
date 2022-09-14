package com.example.zftidemo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainTabFragmentAdapter extends FragmentPagerAdapter {
    private Fragment[] fragments;
    public MainTabFragmentAdapter(FragmentManager fm){
        super(fm);
    }
    public MainTabFragmentAdapter(FragmentManager fm, Fragment[] fragments){
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.length;
    }
}
