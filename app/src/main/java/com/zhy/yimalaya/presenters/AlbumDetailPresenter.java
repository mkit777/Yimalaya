package com.zhy.yimalaya.presenters;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.zhy.yimalaya.api.XimalayaApi;
import com.zhy.yimalaya.interfaces.IAlbumDetailCallback;
import com.zhy.yimalaya.interfaces.IAlbumDetailPresenter;
import com.zhy.yimalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AlbumDetailPresenter implements IAlbumDetailPresenter, IDataCallBack<TrackList> {

    private static final String TAG = "AlbumDetailPresenter";
    private static final int DEFAULT_PAGE = 1;

    private static IAlbumDetailPresenter instance = new AlbumDetailPresenter();
    private int currentPage = DEFAULT_PAGE;
    private boolean isLoadMore = false;

    private List<Track> buffer = new ArrayList<>();

    public static IAlbumDetailPresenter getInstance() {
        return instance;
    }


    private List<IAlbumDetailCallback> mCallbacks = new LinkedList<>();

    private Album mTargetAlbum;

    public void setTargetAlbum(Album targetAlbum) {
        currentPage = 1;
        isLoadMore = false;
        mTargetAlbum = targetAlbum;
    }

    private AlbumDetailPresenter() {
    }


    private void checkStatus() {
        if (mTargetAlbum == null) {
            throw new IllegalStateException("AlbumDetailPresenter 还没有设置 TargetAlbum");
        }
    }

    @Override
    public void getAlumDetail() {
        checkStatus();
        doLoad(currentPage);
    }

    @Override
    public void loadMore() {
        checkStatus();
        isLoadMore = true;
        doLoad(currentPage + 1);
    }

    @Override
    public List<Track> getBufferedList() {
        return buffer;
    }

    private void doLoad(int page) {
        if (!isLoadMore) {
            fireBeforeRequest();
        }
        XimalayaApi.getInstance().getTrackDetail(this, mTargetAlbum.getId(), page);
    }

    @Override
    public void onSuccess(TrackList trackList) {
        if (isLoadMore) {
            buffer.addAll(trackList.getTracks());
            fireOnLoadMore(buffer);
            currentPage++;
        } else {
            buffer.clear();
            buffer.addAll(trackList.getTracks());
            fireOnTrackListLoaded(trackList);
        }
        isLoadMore = false;
    }

    @Override
    public void onError(int i, String s) {
        if (isLoadMore) {
            return;
        }
        fireOnError(i, s);
    }


    private void fireBeforeRequest() {
        LogUtil.d(TAG, String.format("专辑声音列表开始加载 id=%s", mTargetAlbum.getId()));
        for (IAlbumDetailCallback callback : mCallbacks) {
            callback.beforeRequest();
        }
    }

    private void fireOnError(int code, String error) {
        LogUtil.d(TAG, String.format("专辑声音列表开始加载 id=%s  code=%s  error=%s", mTargetAlbum.getId(), code, error));
        for (IAlbumDetailCallback callback : mCallbacks) {
            callback.onNetworkError();
        }
    }

    private void fireOnTrackListLoaded(TrackList trackList) {
        LogUtil.d(TAG, String.format("专辑声音列表开始加载 id=%s  track_counts=%s", mTargetAlbum.getId(), trackList.getTracks().size()));
        for (IAlbumDetailCallback callback : mCallbacks) {
            callback.onTrackListLoaded(trackList);
        }
    }

    private void fireOnLoadMore(List<Track> trackList) {
        LogUtil.d(TAG, String.format("专辑声音列表开始加载 id=%s  track_counts=%s", mTargetAlbum.getId(), trackList.size()));
        for (IAlbumDetailCallback callback : mCallbacks) {
            callback.onLoadMore(trackList);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDetailCallback albumDetailCallback) {
        if (!mCallbacks.contains(albumDetailCallback)) {
            mCallbacks.add(albumDetailCallback);
            // LogUtil.d(TAG, String.format("专辑详情回调注册成功 id=%s", mTargetAlbum.getId()));
            if (mTargetAlbum != null) {
                albumDetailCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unregisterViewCallback(IAlbumDetailCallback albumDetailCallback) {
        mCallbacks.remove(albumDetailCallback);
        LogUtil.d(TAG, String.format("专辑详情回调删除成功 id=%s", mTargetAlbum.getId()));
    }
}
