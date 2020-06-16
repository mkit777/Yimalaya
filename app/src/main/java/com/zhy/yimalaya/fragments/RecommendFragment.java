package com.zhy.yimalaya.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.zhy.yimalaya.DetailActivity;
import com.zhy.yimalaya.R;
import com.zhy.yimalaya.adapters.AlbumListAdapter;
import com.zhy.yimalaya.base.BaseFragment;
import com.zhy.yimalaya.interfaces.IRecommendCallback;
import com.zhy.yimalaya.interfaces.IRecommendPresenter;
import com.zhy.yimalaya.presenters.RecommendPresenter;
import com.zhy.yimalaya.views.UiLoader;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendCallback, UiLoader.OnRefreshListener, AlbumListAdapter.OnItemClickListener {
    private static final String TAG = "RecommendFragment";

    private UiLoader mUiLoader;
    private RecyclerView mRecyclerView;
    private AlbumListAdapter mListAdapter;
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
        mRecommendPresenter.registerViewCallback(this);
        mRecommendPresenter.getRecommendList();
    }

    private View createSuccessView(LayoutInflater inflater, ViewGroup parent) {
        TwinklingRefreshLayout wrapper = null;
        if (mRecyclerView == null) {
            // 获取 RecyclerView
            wrapper = (TwinklingRefreshLayout) inflater.inflate(R.layout.fragment_recommend, parent, false);
            wrapper.setPureScrollModeOn();

            mRecyclerView = wrapper.findViewById(R.id.recycler_view);

            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mListAdapter = new AlbumListAdapter();
            mListAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mListAdapter);
        }
        return wrapper;
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
            mRecommendPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onRefresh() {
        mRecommendPresenter.getRecommendList();
    }

    @Override
    public void onItemClick(int position, Album album) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("album", album);
        startActivity(intent);
    }
}
