package com.zhy.yimalaya.interfaces;

public interface IBasePresenter<C> {
    void registerViewCallback(C c);

    void unregisterViewCallback(C c);
}
