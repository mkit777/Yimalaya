package com.zhy.yimalaya.adapters;

import android.nfc.Tag;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.zhy.yimalaya.utils.FragmentCreator;
import com.zhy.yimalaya.utils.LogUtil;

public class MainContentAdapter extends FragmentPagerAdapter {


    private static final String TAG = "MainContentAdapter";

    public MainContentAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        LogUtil.d(TAG, "position=" + position);
        return FragmentCreator.getFragment(position);
    }

    @Override
    public int getCount() {
        return FragmentCreator.getCount();
    }
}
