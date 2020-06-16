package com.zhy.yimalaya.interfaces;

public interface ISearchPresenter extends IBasePresenter<ISearchCallBack>{

    /**
     * 根据关键词搜索
     * @param keywords 搜索关键词
     */
    void search(String keywords);

    /**
     * 重新搜索上一次内容
     */
    void research();


    /**
     * 加载更多
     */
    void loadMore();

    /**
     * 获取联想搜索词
     * @param qs
     */
    void getSuggestWords(String qs);


    /**
     * 获取搜索热词
     */
    void getHotWord();
}
