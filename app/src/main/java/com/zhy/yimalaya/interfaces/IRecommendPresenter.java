package com.zhy.yimalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IRecommendPresenter extends IBasePresenter<IRecommendCallback>{
    void getRecommendList();


    List<Album> getCurrentRecommendList();
}
