package com.zhy.yimalaya.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.zhy.yimalaya.PlayerActivity;
import com.zhy.yimalaya.R;
import com.zhy.yimalaya.adapters.TrackListAdapter;
import com.zhy.yimalaya.base.BaseFragment;
import com.zhy.yimalaya.interfaces.IHistoryCallback;
import com.zhy.yimalaya.interfaces.IHistoryPresenter;
import com.zhy.yimalaya.interfaces.IPlayerCallBack;
import com.zhy.yimalaya.interfaces.IPlayerPresenter;
import com.zhy.yimalaya.presenters.HistoryPresenter;
import com.zhy.yimalaya.presenters.PlayerPresenter;
import com.zhy.yimalaya.utils.LogUtil;
import com.zhy.yimalaya.views.HistoryDialog;
import com.zhy.yimalaya.views.UiLoader;

import java.util.Date;
import java.util.List;
import java.util.zip.CRC32;

public class HistoryFragment extends BaseFragment implements IHistoryCallback, TrackListAdapter.OnItemClickListener, HistoryDialog.OnDialogConfirmed {

    private static final String TAG = "HistoryFragment";
    private RecyclerView mTrackContainer;
    private TrackListAdapter mTrackListAdapter;
    private IHistoryPresenter mHistoryPresenter;
    private UiLoader mUiLoader;
    private List<Track> mTracks;
    private HistoryDialog mDialog;

    @Override
    public View onCreateSubView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView(inflater, container);
        initPresenter();
        return mUiLoader;
    }

    private void initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        TwinklingRefreshLayout refreshLayout = view.findViewById(R.id.refresh_wrapper);
        refreshLayout.setPureScrollModeOn();

        mTrackContainer = view.findViewById(R.id.track_container);
        mTrackContainer.setLayoutManager(new LinearLayoutManager(container.getContext()));

        mTrackListAdapter = new TrackListAdapter();
        mTrackListAdapter.setOnItemClickListener(this);
        mTrackContainer.setAdapter(mTrackListAdapter);

        mUiLoader = new UiLoader(container.getContext()) {
            @Override
            protected View getSuccessView(LayoutInflater inflater, ViewGroup parent) {
                return view;
            }
        };

        mDialog = new HistoryDialog(getContext());
        mDialog.setOnConfirmListener(this);
    }

    private void initPresenter() {

        mHistoryPresenter = HistoryPresenter.getInstance();
        mHistoryPresenter.registerViewCallback(this);
        mHistoryPresenter.getAllHistory();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    //-------- IHistoryPresenter 结果回调 ------
    @Override
    public void onAddHistoryResult(boolean success) {
        mHistoryPresenter.getAllHistory();
    }

    @Override
    public void onDeleteHistoryResult(boolean success) {
        mHistoryPresenter.getAllHistory();
    }

    @Override
    public void onGetAllHistory(List<Track> tracks) {
        if (mTrackListAdapter != null) {
            mUiLoader.updateState(tracks.isEmpty() ? UiLoader.State.EMPTY : UiLoader.State.SUCCESS);
            mTracks = tracks;
            mTrackListAdapter.setTrackList(tracks);
        }
    }

    @Override
    public void clearAllHistory(boolean success) {
        mHistoryPresenter.getAllHistory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHistoryPresenter.unregisterViewCallback(this);
    }

    // ------------- TrackListAdapter 点击回调 -------
    @Override
    public void onItemClicked(int index, Track track) {
        PlayerPresenter.getInstance().setPlayList(mTracks, index);
        Toast.makeText(getContext(), String.valueOf(index), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), PlayerActivity.class));
    }

    private Track currentTrack = null;

    @Override
    public void onItemLongClicked(int index, Track track) {
        currentTrack = track;
        mDialog.show();
    }

    // ------------ HistoryDialog 回调 ----
    @Override
    public void onConfirmed(boolean clearAll, boolean confirm) {
        if (clearAll) {
            mHistoryPresenter.clearAllHistory();
        } else {
            mHistoryPresenter.deleteHistory(currentTrack);
            currentTrack = null;
        }
    }
}
