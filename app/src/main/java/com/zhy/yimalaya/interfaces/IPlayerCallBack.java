package com.zhy.yimalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallBack {

    /**
     * 播放器准备就绪
     */
    void onSoundPrepared();

    /**
     * 开始播放
     */
    void onPlayStarted();

    /**
     * 暂停播放
     */
    void onPlayPaused();

    /**
     * 播放列表加载完成
     *
     * @param list
     */
    void onListRefreshed(List<Track> list);

    /**
     * 播放模式改变
     */
    void onPlayModeChanged(XmPlayListControl.PlayMode mode);

    /**
     * 进度条改变
     */
    void onPlayProgressChanged(int current, int duration);

    /**
     * 歌曲切换
     */
    void onTrackSwitched(Track track);

    /**
     * 播放顺序切换
     */
    void onOrderChanged(boolean isReverse, int index);
}
