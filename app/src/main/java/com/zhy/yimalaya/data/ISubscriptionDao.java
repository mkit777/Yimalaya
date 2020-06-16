package com.zhy.yimalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface ISubscriptionDao {
    /**
     * 添加订阅内容
     *
     * @param album
     */
    void add(Album album);

    /**
     * 删除订阅内容
     *
     * @param album
     */
    void remove(Album album);

    /**
     * 获取所有订阅内容
     */
    void listAll();


    void getByAlbumId(long id);


    /**
     * 设置结果回调
     * @param callBack
     */
    void setCallBack(ISubscriptionDaoCallBack callBack);
}
