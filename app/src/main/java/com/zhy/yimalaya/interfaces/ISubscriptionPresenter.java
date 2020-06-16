package com.zhy.yimalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallBack> {

    /**
     * 添加订阅
     *
     * @param album
     */
    void add(Album album);

    /**
     * 取消订阅
     *
     * @param album
     */
    void remove(Album album);

    /**
     * 获取所有订阅结果
     */
    void getAll();

    /**
     * 是否已经订阅指定专辑
     *
     * @return
     */
    void isSub(Album album);
}
