package com.zhy.yimalaya.presenters;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;
import com.zhy.yimalaya.api.XimalayaApi;
import com.zhy.yimalaya.interfaces.ISearchCallBack;
import com.zhy.yimalaya.interfaces.ISearchPresenter;
import com.zhy.yimalaya.utils.LogUtil;

import java.util.LinkedList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private static final int DEFAULT_SEARCH_PAGE = 1;
    private static final String TAG = "SearchPresenter";

    private static ISearchPresenter instance = null;
    private String mCurrentSearchKeywords;
    private int mCurrentPage = DEFAULT_SEARCH_PAGE;

    public static ISearchPresenter getInstance() {
        if (instance == null) {
            synchronized (SearchPresenter.class) {
                if (instance == null) {
                    instance = new SearchPresenter();
                }
            }
        }
        return instance;
    }

    private SearchPresenter() {

    }

    private List<ISearchCallBack> mCallBackList = new LinkedList<>();


    @Override
    public void search(String keywords) {
        mCurrentSearchKeywords = keywords;
        doSearch(keywords);
    }

    @Override
    public void research() {
        doSearch(mCurrentSearchKeywords);
    }

    private void doSearch(String keywords) {
        mCurrentPage = DEFAULT_SEARCH_PAGE;
        XimalayaApi.getInstance().search(keywords, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                for (ISearchCallBack callBack : mCallBackList) {
                    callBack.onSearchResult(searchAlbumList.getAlbums());
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "搜索失败 " + s);
                for (ISearchCallBack callBack : mCallBackList) {
                    callBack.OnSearchError();
                }
            }
        });
    }

    @Override
    public void loadMore() {
        XimalayaApi.getInstance().search(mCurrentSearchKeywords, ++mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {

                boolean hasMore = searchAlbumList.getTotalCount() > 0;

                if (!hasMore) {
                    mCurrentPage--;
                }

                for (ISearchCallBack callBack : mCallBackList) {
                    callBack.onLoadMoreResult(searchAlbumList.getAlbums(), hasMore);
                }
            }

            @Override
            public void onError(int i, String s) {
                mCurrentPage--;
                LogUtil.d(TAG, String.format("加载更多失败 code=%s  msg=%s", i, s));
            }
        });
    }

    @Override
    public void getSuggestWords(String qs) {
        XimalayaApi.getInstance().getSuggestWords(qs, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                for (ISearchCallBack callBack : mCallBackList) {
                    callBack.onLoadSuggestWordResult(suggestWords.getKeyWordList());
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "关联词获取失败 " + s);
            }
        });
    }

    @Override
    public void getHotWord() {
        XimalayaApi.getInstance().getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                for (ISearchCallBack callBack : mCallBackList) {
                    callBack.onLoadHotWords(hotWordList.getHotWordList());
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "热词加载失败 " + s);
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallBack callBack) {
        if (!mCallBackList.contains(callBack)) {
            mCallBackList.add(callBack);
        }
    }

    @Override
    public void unregisterViewCallback(ISearchCallBack callBack) {
        mCallBackList.remove(callBack);
    }
}
