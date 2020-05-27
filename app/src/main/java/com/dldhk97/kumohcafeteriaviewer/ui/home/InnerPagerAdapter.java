package com.dldhk97.kumohcafeteriaviewer.ui.home;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class InnerPagerAdapter extends FragmentStatePagerAdapter {
    private final ArrayList<InnerFragment> pages;

    public InnerPagerAdapter(FragmentManager fm, ArrayList<InnerFragment> pages) {
        super(fm);
        this.pages = pages;
    }
    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }
    @Override
    public int getCount() {
        return pages.size();
    }
}