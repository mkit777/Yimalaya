package com.zhy.yimalaya.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.zhy.yimalaya.DetailActivity;
import com.zhy.yimalaya.R;
import com.zhy.yimalaya.adapters.AlbumListAdapter;
import com.zhy.yimalaya.base.BaseFragment;
import com.zhy.yimalaya.interfaces.ISubscriptionCallBack;
import com.zhy.yimalaya.presenters.SubscriptionPresenter;
import com.zhy.yimalaya.utils.LogUtil;
import com.zhy.yimalaya.views.ConfirmDialog;
import com.zhy.yimalaya.views.UiLoader;

import java.util.ArrayList;
import java.util.List;

public class SubscribeFragment extends BaseFragment implements ISubscriptionCallBack, AlbumListAdapter.OnItemClickListener, ConfirmDialog.OnConfirmListener {


    private static final String TAG = "SubscribeFragment";
    private View mSubView;
    private RecyclerView mAlbumContainer;
    private AlbumListAdapter mAlbumListAdapter;
    private SubscriptionPresenter mSubscriptionPresenter;
    private ConfirmDialog mDialog;

    private Album currentAlbum = null;
    private UiLoader mUiLoader;


    @Override
    public View onCreateSubView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSubView = inflater.inflate(R.layout.fragment_subscribe, container, false);
        mUiLoader = new UiLoader(container.getContext()) {
            @Override
            protected View getSuccessView(LayoutInflater inflater, ViewGroup parent) {
                return mSubView;
            }
        };

        initView(container);
        setupEvent();
        initPresenter();
        return mUiLoader;
    }

    private void setupEvent() {
        mDialog.setOnConfirmListener(this);
    }

    private void initPresenter() {
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getAll();
    }

    private void initView(ViewGroup container) {
        TwinklingRefreshLayout refreshWrapper = mSubView.findViewById(R.id.refresh_wrapper);
        refreshWrapper.setPureScrollModeOn();

        mAlbumContainer = mSubView.findViewById(R.id.album_container);
        mAlbumContainer.setLayoutManager(new LinearLayoutManager(container.getContext()));

        mAlbumListAdapter = new AlbumListAdapter();
        mAlbumListAdapter.setOnItemClickListener(this);
        mAlbumContainer.setAdapter(mAlbumListAdapter);

        mDialog = new ConfirmDialog(getContext());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSubscriptionPresenter.unregisterViewCallback(this);
        LogUtil.d(TAG, "onDestroy");
    }


    // ----------------  SubscriptionCallBack -----------------------
    @Override
    public void onAddResult(boolean success) {
        mSubscriptionPresenter.getAll();
    }

    @Override
    public void onRemoveResult(boolean success) {
        mSubscriptionPresenter.getAll();
    }

    @Override
    public void onLoaded(List<Album> albums) {
        if (albums.isEmpty()) {
            mUiLoader.updateState(UiLoader.State.EMPTY);
            return;
        }
        mUiLoader.updateState(UiLoader.State.SUCCESS);
        mAlbumListAdapter.setData(albums);
    }

    @Override
    public void onSubCheckResult(boolean sub) {

    }

    @Override
    public void onSubFull() {
        Toast.makeText(getContext(), "订阅已满", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position, Album album) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("album", album);
        startActivity(intent);
    }

    @Override
    public void onItemLongClicked(int position, Album album) {
        Toast.makeText(getContext(), "长按了" + position, Toast.LENGTH_SHORT).show();
        currentAlbum = album;
        mDialog.show();
    }

    // ================= 对话框回调 ===================
    @Override
    public void onConfirm(boolean confirm) {
        Toast.makeText(getContext(), confirm ? "确认" : "取消", Toast.LENGTH_SHORT).show();
        if (confirm) {
            mSubscriptionPresenter.remove(currentAlbum);
        }

        currentAlbum = null;
    }
}
