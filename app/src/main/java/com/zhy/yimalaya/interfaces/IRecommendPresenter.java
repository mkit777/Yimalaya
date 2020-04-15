package com.zhy.yimalaya.interfaces;

public interface IRecommendPresenter {
    void getRecommendList();

    void registerResultCallback(IRecommendCallback callback);

    void unregisterResultCallback(IRecommendCallback callback);
}
