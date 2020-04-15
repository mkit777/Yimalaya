package com.zhy.yimalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import java.util.List;

public interface IRecommendCallback {
    void beforeRequest();

    void onSuccess(List<Album> data);

    void onFailed();

    void onEmpty();
}
