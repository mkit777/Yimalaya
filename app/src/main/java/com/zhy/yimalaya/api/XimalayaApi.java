package com.zhy.yimalaya.api;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class XimalayaApi {

    private static final int DEFAULT_LIKE_COUNT = 20;
    private static final int DEFAULT_HOT_WORDS_COUNT = 20;
    private static final int DEFAULT_SEARCH_PAGE_SIZE = 20;

    private XimalayaApi() {

    }

    private static XimalayaApi instance = null;


    public static XimalayaApi getInstance() {
        if (instance == null) {
            synchronized (XimalayaApi.class) {
                if (instance == null) {
                    instance = new XimalayaApi();
                }
            }
        }
        return instance;
    }


    public void gerRecommendList(IDataCallBack<GussLikeAlbumList> callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.LIKE_COUNT, String.valueOf(DEFAULT_LIKE_COUNT));
        CommonRequest.getGuessLikeAlbum(map, callBack);
    }


    public void getTrackDetail(IDataCallBack<TrackList> callBack, long albumId, int page) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        CommonRequest.getTracks(map, callBack);
    }


    public void search(String keywords, int page, IDataCallBack<SearchAlbumList> callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, keywords);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        map.put(DTransferConstants.PAGE_SIZE, String.valueOf(DEFAULT_SEARCH_PAGE_SIZE));
        CommonRequest.getSearchedAlbums(map, callBack);
    }


    public void getSuggestWords(String queryString, IDataCallBack<SuggestWords> callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, queryString);
        CommonRequest.getSuggestWord(map, callBack);
    }

    public void getHotWords(IDataCallBack<HotWordList> callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, String.valueOf(DEFAULT_HOT_WORDS_COUNT));
        CommonRequest.getHotWords(map, callBack);
    }


}
