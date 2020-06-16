package com.zhy.yimalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallBack {

    /**
     * 搜索结果回调
     *
     * @param albums 专辑列表
     */
    void onSearchResult(List<Album> albums);

    /**
     * 搜索失败
     */
    void OnSearchError();

    /**
     * 加载更多结果回调
     *
     * @param albums 专辑列表
     * @param isOk   true成功获取更多，false 无内多内容
     */
    void onLoadMoreResult(List<Album> albums, boolean isOk);


    /**
     * 联想词结果回调
     *
     * @param keywords 联想词
     */
    void onLoadSuggestWordResult(List<QueryResult> keywords);

    /**
     * 搜索热词结果回调
     *
     * @param hotWords 搜索热词
     */
    void onLoadHotWords(List<HotWord> hotWords);

}
