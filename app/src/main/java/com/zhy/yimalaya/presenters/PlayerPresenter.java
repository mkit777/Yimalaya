package com.zhy.yimalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.zhy.yimalaya.base.BaseApplication;
import com.zhy.yimalaya.data.HistoryDao;
import com.zhy.yimalaya.interfaces.IPlayerCallBack;
import com.zhy.yimalaya.interfaces.IPlayerPresenter;
import com.zhy.yimalaya.utils.LogUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final PlayerPresenter INSTANCE = new PlayerPresenter();
    private static final String TAG = "PlayerPresenter";

    private Set<IPlayerCallBack> mCallBacks = new HashSet<>();
    private final SharedPreferences mPreferences;

    private boolean isReverse = false;


    public static PlayerPresenter getInstance() {
        return INSTANCE;
    }

    private XmPlayerManager mPlayerManager;

    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getContext());
        mPlayerManager.addAdsStatusListener(this);
        mPlayerManager.addPlayerStatusListener(this);
        mPreferences = BaseApplication.getContext().getSharedPreferences("PlayMode", Context.MODE_PRIVATE);
    }

    @Override
    public void play() {
        mPlayerManager.play();
    }

    @Override
    public void pause() {
        mPlayerManager.pause();
    }


    @Override
    public void playPrev() {
        mPlayerManager.playPre();
    }

    @Override
    public void playNext() {
        mPlayerManager.playNext();
    }

    @Override
    public void switchOrder() {
        isReverse = !isReverse;

        List<Track> playList = mPlayerManager.getPlayList();
        int size = playList.size();
        int cur = mPlayerManager.getCurrentIndex();
        int next = size - 1 - cur;

        Collections.reverse(playList);
        mPlayerManager.setPlayList(playList, next);

        for (IPlayerCallBack callBack : mCallBacks) {
            callBack.onListRefreshed(playList);
            callBack.onOrderChanged(isReverse, next);
        }
    }


    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        mPlayerManager.setPlayMode(mode);
        for (IPlayerCallBack callBack : mCallBacks) {
            callBack.onPlayModeChanged(mode);
        }
        mPreferences.edit().putInt("PlayMode", mode.ordinal()).apply();
    }


    public Track getCurrentTrack() {
        PlayableModel current = mPlayerManager.getCurrSound();

        if (current instanceof Track) {
            return (Track) current;
        }

        if (current == null) {
            List<Track> playList = getPlayList();
            if (playList == null || playList.isEmpty()) {
                return null;
            } else {
                return playList.get(getCurrentIndex());
            }
        }
        return null;
    }

    @Override
    public boolean isFirst() {
        LogUtil.d(TAG, "current index=" + mPlayerManager.getCurrentIndex());
        return mPlayerManager.getCurrentIndex() == 0;
    }

    @Override
    public boolean isLast() {
        return mPlayerManager.getCurrentIndex() == mPlayerManager.getPlayList().size() - 1;
    }

    @Override
    public XmPlayListControl.PlayMode getCurrentMode() {
        return mPlayerManager.getPlayMode();
    }

    @Override
    public int getCurrentIndex() {
        return mPlayerManager.getCurrentIndex();
    }

    public void setPlayList(List<Track> tracks, int startIndex) {
        if (tracks != null) {
            mPlayerManager.setPlayList(tracks, startIndex);
            for (IPlayerCallBack callBack : mCallBacks) {
                callBack.onListRefreshed(tracks);
            }
        } else {
            LogUtil.d(TAG, "mPlayerManager is null");
        }
    }


    @Override
    public List<Track> getPlayList() {
        return mPlayerManager.getPlayList();
    }

    @Override
    public boolean hasPlayList() {
        return mPlayerManager.getPlayList() != null && mPlayerManager.getPlayList().size() > 0;
    }

    @Override
    public void playByIndex(int index) {
        mPlayerManager.play(index);
    }

    @Override
    public void seekTo(int progress) {
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        return mPlayerManager.isPlaying();
    }


    @Override
    public void registerViewCallback(IPlayerCallBack iPlayerCallBack) {
        List<Track> playList = mPlayerManager.getPlayList();
        if (playList != null) {
            iPlayerCallBack.onListRefreshed(playList);
        }
        iPlayerCallBack.onPlayModeChanged(mPlayerManager.getPlayMode());
        iPlayerCallBack.onTrackSwitched(getCurrentTrack());
        iPlayerCallBack.onOrderChanged(isReverse, mPlayerManager.getCurrentIndex());

        handlePlayState(iPlayerCallBack);
        handlePlayProgress(iPlayerCallBack);


        mCallBacks.add(iPlayerCallBack);
    }

    private void handlePlayProgress(IPlayerCallBack iPlayerCallBack) {
        iPlayerCallBack.onPlayProgressChanged(mPlayerManager.getPlayCurrPositon(), mPlayerManager.getDuration());
    }

    private void handlePlayState(IPlayerCallBack iPlayerCallBack) {
        if (mPlayerManager.isPlaying()) {
            iPlayerCallBack.onPlayStarted();
        } else {
            iPlayerCallBack.onPlayPaused();
        }

    }

    @Override
    public void unregisterViewCallback(IPlayerCallBack iPlayerCallBack) {
        mCallBacks.remove(iPlayerCallBack);
    }

    // ===================  广告回调开始 =====================

    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG, "onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG, "onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG, "onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG, "onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG, "onStartPlayAds index=" + i);
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG, "onCompletePlayAds");
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.d(TAG, "onAdError what=" + what + "  extra=" + extra);
    }
    // ==================  广告回调结束 ====================


    // ================== 音频播放回调开始 ======================

    /**
     * 开始播放
     */
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG, "onPlayStart");
        for (IPlayerCallBack callBack : mCallBacks) {
            callBack.onPlayStarted();
        }
    }

    /**
     * 暂停播放
     */
    @Override
    public void onPlayPause() {
        LogUtil.d(TAG, "onPlayPause");
        for (IPlayerCallBack callBack : mCallBacks) {
            callBack.onPlayPaused();
        }
    }

    /**
     * 音频准备就绪
     */
    @Override
    public void onSoundPrepared() {
        for (IPlayerCallBack callBack : mCallBacks) {
            callBack.onSoundPrepared();
        }
    }

    /**
     * 音频切换
     */
    @Override
    public void onSoundSwitch(PlayableModel last, PlayableModel current) {
        LogUtil.d(TAG, "onSoundSwitch");
        if (current instanceof Track) {
            HistoryPresenter.getInstance().addHistory((Track) current);
            for (IPlayerCallBack callBack : mCallBacks) {
                callBack.onTrackSwitched((Track) current);
            }
        }
    }

    /**
     * 进度条更新
     */
    @Override
    public void onPlayProgress(int currentPos, int duration) {
        LogUtil.d(TAG, String.format("onPlayProgress  currentPos=%s duration=%s", currentPos, duration));
        for (IPlayerCallBack callBack : mCallBacks) {
            callBack.onPlayProgressChanged(currentPos, duration);
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG, "onSoundPlayComplete");
    }

    @Override
    public void onBufferProgress(int pos) {
        LogUtil.d(TAG, "onBufferProgress ===> i=" + pos);
    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG, "onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG, "onBufferingStop");
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG, "onPlayStop");
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG, "onError " + e);
        return false;
    }
    // ==================== 音频播放结束 ========================
}
