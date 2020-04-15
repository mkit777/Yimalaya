package com.zhy.yimalaya.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhy.yimalaya.R;
import com.zhy.yimalaya.base.BaseFragment;

public class SubscribeFragment extends BaseFragment {
    @Override
    public View onCreateSubView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subscribe, container, false);
    }
}
