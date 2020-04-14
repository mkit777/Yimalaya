package com.zhy.yimalaya.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.zhy.yimalaya.R;
import com.zhy.yimalaya.adapters.RecommendListAdapter;
import com.zhy.yimalaya.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

public class RecommendFragment extends BaseFragment {
    private static final String TAG = "RecommendFragment";

    private RecommendListAdapter mListAdapter;

    /**
     * 初始化视图
     */
    @Override
    public View onCreateSubView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 为当前 fragment 加载布局
        View mainView = inflater.inflate(R.layout.fragment_recommend, container, false);

        // 设置 RecyclerView 布局
        RecyclerView mRecyclerView = mainView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        // 设置 RecyclerView 的视图适配器
        mListAdapter = new RecommendListAdapter();
        mRecyclerView.setAdapter(mListAdapter);
        // 加载数据并更新视图适配器
        loadData();
        return mainView;
    }

    /**
     * 加载推荐数据，并更新视图
     */
    private void loadData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "0");
        map.put(DTransferConstants.CALC_DIMENSION, "1");
        CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {

            @Override
            public void onSuccess(AlbumList albumList) {
                LogUtil.d(TAG, "加载推荐列表数据 size=" + albumList.getAlbums().size());
                mListAdapter.setData(albumList.getAlbums());
                mListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "推荐列表数据加载失败" + s);
            }
        });
    }
}
