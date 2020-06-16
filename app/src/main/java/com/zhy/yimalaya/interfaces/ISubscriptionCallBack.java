package com.zhy.yimalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionCallBack {

    /**
     * 添加订阅结果回调
     * @param success
     */
    void onAddResult(boolean success);

    /**
     * 取消订阅结果回调
     * @param success
     */
    void onRemoveResult(boolean success);

    /**
     * 获取订阅列表结果回调
     * @param albums
     */
    void onLoaded(List<Album> albums);

    /**
     * 是否已经订阅
     * @param sub
     */
    void onSubCheckResult(boolean sub);


    /**
     * 订阅已满
     */
    void onSubFull();
}
