package com.zhy.yimalaya.presenters;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.zhy.yimalaya.base.BaseApplication;
import com.zhy.yimalaya.data.ISubscriptionDao;
import com.zhy.yimalaya.data.ISubscriptionDaoCallBack;
import com.zhy.yimalaya.data.SubscriptionDao;
import com.zhy.yimalaya.interfaces.ISubscriptionCallBack;
import com.zhy.yimalaya.interfaces.ISubscriptionPresenter;
import com.zhy.yimalaya.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionPresenter implements ISubscriptionPresenter, ISubscriptionDaoCallBack {

    private static volatile SubscriptionPresenter instance = null;

    public static SubscriptionPresenter getInstance() {
        if (instance == null) {
            synchronized (SubscriptionPresenter.class) {
                if (instance == null) {
                    instance = new SubscriptionPresenter();
                }
            }
        }
        return instance;
    }

    private List<ISubscriptionCallBack> mCallBackList = new ArrayList<>();
    private ISubscriptionDao mSubscriptionDao;

    private int count = 0;

    private SubscriptionPresenter() {
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallBack(this);
        getAll();
    }

    @Override
    public void add(Album album) {
        if (count >= Constants.SUB_MAX_COUNT) {
            mCallBackList.forEach(ISubscriptionCallBack::onSubFull);
            return;
        }

        Observable.create(emitter -> mSubscriptionDao.add(album))
                .subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void remove(Album album) {
        Observable.create(emitter -> mSubscriptionDao.remove(album))
                .subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getAll() {
        Observable.create(emitter -> {
            // 只调用，不处理
            mSubscriptionDao.listAll();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void isSub(Album album) {
        Observable.create(emitter -> {
            // 只调用，不处理
            mSubscriptionDao.getByAlbumId(album.getId());
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(ISubscriptionCallBack callBack) {
        if (!mCallBackList.contains(callBack)) {
            mCallBackList.add(callBack);
        }
    }

    @Override
    public void unregisterViewCallback(ISubscriptionCallBack callBack) {
        mCallBackList.remove(callBack);
    }

    //---------------- DAO 结果回调 --------------------
    @Override
    public void onAdd(boolean success) {
        BaseApplication.handler.post(() -> {
            for (ISubscriptionCallBack callBack : mCallBackList) {
                callBack.onAddResult(success);
            }
        });
    }

    @Override
    public void onRemove(boolean success) {
        BaseApplication.handler.post(() -> {
            for (ISubscriptionCallBack callBack : mCallBackList) {
                callBack.onRemoveResult(success);
            }
        });
    }

    @Override
    public void onListResult(List<Album> albums) {
        count = albums.size();
        BaseApplication.handler.post(() -> {
            for (ISubscriptionCallBack callBack : mCallBackList) {
                callBack.onLoaded(albums);
            }
        });
    }

    @Override
    public void onAlbumResult(Album album) {
        for (ISubscriptionCallBack callBack : mCallBackList) {
            callBack.onSubCheckResult(album != null);
        }
    }



}
