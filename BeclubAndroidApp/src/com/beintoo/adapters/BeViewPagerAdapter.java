package com.beintoo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.beintoo.fragments.BeMapFragment;

public class BeViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment fragment0;
    private Fragment fragment1;
    private FragmentManager fm;

    public BeViewPagerAdapter(FragmentManager fm, Fragment fragment0) {
        super(fm);
        this.fm = fm;
        this.fragment0 = fragment0;
        this.fragment1 = new BeMapFragment();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return fragment0;
        } else {
            return fragment1;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void refreshFragment(Fragment newFragment) {
        fm.beginTransaction().remove(this.fragment0).commit();
//        fm.beginTransaction().remove(this.fragment1).commit();
        this.fragment0 = newFragment;
//        this.fragment1 = new BeMapFragment();
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
