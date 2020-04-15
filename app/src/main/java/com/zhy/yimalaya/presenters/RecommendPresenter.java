package com.zhy.yimalaya.presenters;

import androidx.arch.core.util.Function;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.zhy.yimalaya.interfaces.IRecommendCallback;
import com.zhy.yimalaya.interfaces.IRecommendPresenter;
import com.zhy.yimalaya.utils.LogUtil;

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


    private RecommendPresenter() {

    }

    private List<IRecommendCallback> callbacks = new LinkedList<>();

    public void getRecommendList() {
        fireBeforeRequest();
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.CATEGORY_ID, "0");
        map.put(DTransferConstants.CALC_DIMENSION, "1");
        CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList albumList) {
                if (albumList.getAlbums().isEmpty()) {
                    fireOnEmpty();
                } else {
                    fireOnSuccess(albumList.getAlbums());
                }
            }

            @Override
            public void onError(int i, String s) {
                fireOnError();
            }
        });
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

    private void fireOnError() {
        LogUtil.d(TAG, "推荐数据加载失败");
        for (IRecommendCallback callback : callbacks) {
            callback.onFailed();
        }
    }

    @Override
    public void registerResultCallback(IRecommendCallback callback) {
        if (callbacks.contains(callback)) {
            LogUtil.d(TAG, "该回调函数已经注册");
            return;
        }
        callbacks.add(callback);
        LogUtil.d(TAG, "数据回调注册成功");
    }

    @Override
    public void unregisterResultCallback(IRecommendCallback callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
            LogUtil.d(TAG, "数据回调已取消");
        }
    }
}