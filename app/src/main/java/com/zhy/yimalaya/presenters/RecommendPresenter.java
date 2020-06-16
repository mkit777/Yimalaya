package com.zhy.yimalaya.presenters;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.zhy.yimalaya.api.XimalayaApi;
import com.zhy.yimalaya.interfaces.IRecommendCallback;
import com.zhy.yimalaya.interfaces.IRecommendPresenter;
import com.zhy.yimalaya.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 加载推荐数据
 */
public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";

    private static IRecommendPresenter instance = new RecommendPresenter();

    public static IRecommendPresenter getInstance() {
        return instance;
    }


    private List<Album> currentRecommendList = new ArrayList<>();

    private RecommendPresenter() {

    }

    private List<IRecommendCallback> callbacks = new LinkedList<>();

    public void getRecommendList() {
        fireBeforeRequest();
        XimalayaApi.getInstance().gerRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList.getAlbumList().isEmpty()) {
                    fireOnEmpty();
                } else {
                    currentRecommendList.clear();
                    currentRecommendList.addAll(gussLikeAlbumList.getAlbumList());
                    fireOnSuccess(gussLikeAlbumList.getAlbumList());
                }
            }

            @Override
            public void onError(int code, String error) {
                fireOnError(code, error);
            }
        });
    }

    @Override
    public List<Album> getCurrentRecommendList() {
        return currentRecommendList;
    }


    private void fireBeforeRequest() {
        LogUtil.d(TAG, "开始加载推荐数据");
        for (IRecommendCallback callback : callbacks) {
            callback.beforeRequest();
        }
    }

    private void fireOnSuccess(List<Album> albumLists) {
        LogUtil.d(TAG, "推荐数据加载成功 size=" + albumLists.size());
        for (IRecommendCallback callback : callbacks) {
            callback.onSuccess(albumLists);
        }

    }

    private void fireOnEmpty() {
        LogUtil.d(TAG, "推荐数据为空");
        for (IRecommendCallback callback : callbacks) {
            callback.onEmpty();
        }
    }

    private void fireOnError(int code, String error) {
        LogUtil.d(TAG, String.format("推荐数据加载失败 code=%s  error=%s", code, error));
        for (IRecommendCallback callback : callbacks) {
            callback.onFailed();
        }
    }

    @Override
    public void registerViewCallback(IRecommendCallback callback) {
        if (callbacks.contains(callback)) {
            LogUtil.d(TAG, "该回调函数已经注册");
            return;
        }
        callbacks.add(callback);
        LogUtil.d(TAG, "数据回调注册成功");
    }

    @Override
    public void unregisterViewCallback(IRecommendCallback callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
            LogUtil.d(TAG, "数据回调已取消");
        }
    }
}