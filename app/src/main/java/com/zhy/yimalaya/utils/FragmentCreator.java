package com.zhy.yimalaya.utils;

import androidx.fragment.app.Fragment;

import com.zhy.yimalaya.fragments.HistoryFragment;
import com.zhy.yimalaya.fragments.RecommendFragment;
import com.zhy.yimalaya.fragments.SubscribeFragment;

import java.util.HashMap;
import java.util.function.BiFunction;

public class FragmentCreator {
    private static final String TAG = "FragmentCreator";
    private static final int RECOMMEND_FRAGMENT_INDEX = 0;
    private static final int SUBSCRIBE_FRAGMENT_INDEX = 1;
    private static final int HISTORY_FRAGMENT_INDEX = 2;

    private static final int FRAGMENT_SIZE = 3;

    private static FragmentCreator instance = new FragmentCreator();

    public static Fragment getFragment(int index) {
        LogUtil.d(TAG, String.format("FragmentCreator#getFragment  index=%s", index));
        return instance.getCachedFragment(index);
    }

    public static int getCount() {
        return FRAGMENT_SIZE;
    }

    private FragmentCreator() {

    }

    private HashMap<Integer, Fragment> sCaches = new HashMap<>();


    private Fragment getCachedFragment(int index) {
        return sCaches.compute(index, new BiFunction<Integer, Fragment, Fragment>() {
            @Override
            public Fragment apply(Integer index, Fragment fragment) {
                if (fragment == null) {
                    LogUtil.d(TAG, String.format("创建Fragment index=%s", index));
                    switch (index) {
                        case HISTORY_FRAGMENT_INDEX:
                            return new HistoryFragment();
                        case RECOMMEND_FRAGMENT_INDEX:
                            return new RecommendFragment();
                        case SUBSCRIBE_FRAGMENT_INDEX:
                            return new SubscribeFragment();
                    }
                }
                return fragment;
            }
        });
    }
}
