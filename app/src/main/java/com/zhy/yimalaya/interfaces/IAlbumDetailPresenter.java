package com.zhy.yimalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailCallback>{
    void setTargetAlbum(Album targetAlbum);

    void getAlumDetail();

    void loadMore();

    List<Track> getBufferedList();
}
