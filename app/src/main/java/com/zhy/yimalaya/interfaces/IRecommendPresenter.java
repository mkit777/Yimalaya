package com.zhy.yimalaya.interfaces;

public interface IRecommendPresenter {
    void getRecommendList();

    void register(IRecommendCallback callback);

    void unregister(IRecommendCallback callback);
}
