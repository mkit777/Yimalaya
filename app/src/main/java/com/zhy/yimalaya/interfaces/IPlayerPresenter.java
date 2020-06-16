package com.zhy.yimalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.HashMap;
import java.util.List;

public interface IPlayerPresenter extends IBasePresenter<IPlayerCallBack> {

    /**
     * 播放指定音频
     */
    void playByIndex(int index);

    /**
     * 上一首
     */
    void playPrev();

    /**
     * 下一首
     */
    void playNext();

    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 调整进度条
     */
    void seekTo(int progress);

    /**
     * 顺序切换
     */
    void switchOrder();

    /**
     * 播放模式切换
     */
    void switchPlayMode(XmPlayListControl.PlayMode mode);

    /**
     * 是否正在播放
     *
     * @return
     */
    boolean isPlaying();


    /**
     * 获取当前的音乐索引
     *
     * @return
     */
    int getCurrentIndex();

    /**
     * 当前音乐是否是列表第一
     */
    boolean isFirst();

    /**
     * 当前音乐是否是列表最后一项
     */
    boolean isLast();

    /**
     * 获取当前的音频
     *
     * @return
     */
    Track getCurrentTrack();

    /**
     * 获取当前的播放模式
     */
    XmPlayListControl.PlayMode getCurrentMode();

    /**
     * 获取播放列表
     */
    List<Track> getPlayList();

    /**
     * 是否有可播放的音乐
     */
    boolean hasPlayList();

    /**
     * 设置播放列表
     */
    void setPlayList(List<Track> tracks, int startIndex);
}
