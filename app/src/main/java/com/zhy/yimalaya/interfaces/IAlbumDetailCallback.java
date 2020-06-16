package com.zhy.yimalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.List;

public interface IAlbumDetailCallback {

    void onTrackListLoaded(TrackList trackList);

    void onLoadMore(List<Track> trackList);

    void onAlbumLoaded(Album album);

    void onNetworkError();

    void beforeRequest();
}
