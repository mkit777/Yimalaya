package com.zhy.yimalaya.presenters;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.zhy.yimalaya.base.BaseApplication;
import com.zhy.yimalaya.data.HistoryDao;
import com.zhy.yimalaya.data.IHistoryDao;
import com.zhy.yimalaya.data.IHistoryDaoCallback;
import com.zhy.yimalaya.interfaces.IHistoryCallback;
import com.zhy.yimalaya.interfaces.IHistoryPresenter;
import com.zhy.yimalaya.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private static IHistoryPresenter instance = null;

    public static IHistoryPresenter getInstance() {
        if (instance == null) {
            synchronized (HistoryPresenter.class) {
                if (instance == null) {
                    instance = new HistoryPresenter();
                }
            }
        }
        return instance;
    }

    private IHistoryDao mHistoryDao;
    private List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private List<Track> mData = null;

    private boolean isDeleteByAdd = false;

    private HistoryPresenter() {
        mHistoryDao = HistoryDao.getInstance();
        mHistoryDao.setCallBack(this);
    }


    @Override
    public void addHistory(Track track) {
        Observable.create(e -> {
            // 添加之前先删除
            isDeleteByAdd = true;
            mHistoryDao.deleteHistory(track);
            // 删除完自己后发现已满，则把最后一条给删除
            if (mData.size() >= Constants.MAX_HISTORY_SIZE) {
                isDeleteByAdd = true;
                mHistoryDao.deleteHistory(mData.get(mData.size() - 1));
            }
            mHistoryDao.addHistory(track);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteHistory(Track track) {
        Observable.create(e -> {
            mHistoryDao.deleteHistory(track);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getAllHistory() {
        Observable.create(e -> {
            mHistoryDao.getAllHistory();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void clearAllHistory() {
        Observable.create(e -> {
            mHistoryDao.clearHistory();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(IHistoryCallback callback) {
        mCallbacks.remove(callback);
    }


    // -------------- IHistoryDao 回调接口 -----------------------
    @Override
    public void onAddHistoryResult(boolean success) {
        for (IHistoryCallback callback : mCallbacks) {
            callback.onAddHistoryResult(success);
        }
    }

    @Override
    public void onDeleteHistoryResult(boolean success) {
        if (isDeleteByAdd) {
            isDeleteByAdd = false;
            return;
        }


        BaseApplication.handler.post(() -> {
            for (IHistoryCallback callback : mCallbacks) {
                callback.onDeleteHistoryResult(success);
            }
        });
    }

    @Override
    public void onGetAllHistory(List<Track> tracks) {
        mData = tracks;
        BaseApplication.handler.post(() -> {
            for (IHistoryCallback callback : mCallbacks) {
                callback.onGetAllHistory(tracks);
            }
        });

    }

    @Override
    public void onClearAllHistory(boolean success) {
        BaseApplication.handler.post(() -> {
            for (IHistoryCallback callback : mCallbacks) {
                callback.clearAllHistory(success);
            }
        });
    }
}
