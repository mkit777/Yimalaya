package com.zhy.yimalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryDao {

    /**
     * 添加收听历史
     *
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除收听历史
     *
     * @param track
     */
    void deleteHistory(Track track);


    /**
     * 获取所有收听历史
     */
    void getAllHistory();


    /**
     * 清空收听历史
     */
    void clearHistory();


    /**
     * 设置结果回调
     */
    void setCallBack(IHistoryDaoCallback callBack);
}
