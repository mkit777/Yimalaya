package com.zhy.yimalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionDaoCallBack {

    /**
     * 添加结果回调
     *
     * @param success
     */
    void onAdd(boolean success);


    /**
     * 删除结果回调
     *
     * @param success
     */
    void onRemove(boolean success);


    /**
     * 获得所有结果回调
     *
     * @param albums
     */
    void onListResult(List<Album> albums);

    /**
     * 根据id查询album的结果回调
     *
     * @param album
     */
    void onAlbumResult(Album album);
}
