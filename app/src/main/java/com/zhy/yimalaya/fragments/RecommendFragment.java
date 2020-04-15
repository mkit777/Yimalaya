package com.zhy.yimalaya.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.zhy.yimalaya.R;
import com.zhy.yimalaya.adapters.RecommendListAdapter;
import com.zhy.yimalaya.base.BaseFragment;
import com.zhy.yimalaya.interfaces.IRecommendCallback;
import com.zhy.yimalaya.interfaces.IRecommendPresenter;
import com.zhy.yimalaya.presenters.RecommendPresenter;
import com.zhy.yimalaya.views.UiLoader;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendCallback, UiLoader.OnRefreshListener {
    private static final String TAG = "RecommendFragment";

    private UiLoader mUiLoader;
    private RecyclerView mRecyclerView;
    private RecommendListAdapter mListAdapter;
    private IRecommendPresenter mRecommendPresenter;

    /**
     * 初始化视图
     */
    @Override
    public View onCreateSubView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mUiLoader == null) {
            mUiLoader = new UiLoader(getContext()) {
                @Override
                protected View getSuccessView(LayoutInflater inflater, ViewGroup parent) {
                    return createSuccessView(inflater, parent);
                }
            };
            mUiLoader.setOnRefreshClickListener(this);
        }
        return mUiLoader;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecommendPresenter = RecommendPresenter.getInstance();
        mRecommendPresenter.register(this);
        mRecommendPresenter.getRecommendList();
    }

    private View createSuccessView(LayoutInflater inflater, ViewGroup parent) {
        if (mRecyclerView == null) {
            // 获取 RecyclerView
            mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_recommend, parent, false);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mListAdapter = new RecommendListAdapter();
            mRecyclerView.setAdapter(mListAdapter);
        }
        return mRecyclerView;
    }


    @Override
    public void beforeRequest() {
        mUiLoader.updateState(UiLoader.State.LOADING);
    }

    @Override
    public void onSuccess(List<Album> data) {
        mUiLoader.updateState(UiLoader.State.SUCCESS);
        mListAdapter.setData(data);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEmpty() {
        mUiLoader.updateState(UiLoader.State.EMPTY);
    }

    @Override
    public void onFailed() {
        mUiLoader.updateState(UiLoader.State.NETWORK_ERROR);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unregister(this);
        }
    }

    @Override
    public void onRefresh() {
        mRecommendPresenter.getRecommendList();
    }
}
