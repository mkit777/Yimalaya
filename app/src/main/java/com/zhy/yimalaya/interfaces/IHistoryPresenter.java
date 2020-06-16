package com.zhy.yimalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryPresenter extends IBasePresenter<IHistoryCallback> {

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
     * 获取所有历史记录
     */
    void getAllHistory();


    /**
     * 清除所有历史记录
     */
    void clearAllHistory();
}
