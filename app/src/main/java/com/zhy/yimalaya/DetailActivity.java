package com.zhy.yimalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.zhy.yimalaya.adapters.TrackListAdapter;
import com.zhy.yimalaya.interfaces.IAlbumDetailCallback;
import com.zhy.yimalaya.interfaces.IAlbumDetailPresenter;
import com.zhy.yimalaya.interfaces.IPlayerCallBack;
import com.zhy.yimalaya.interfaces.ISubscriptionCallBack;
import com.zhy.yimalaya.presenters.AlbumDetailPresenter;
import com.zhy.yimalaya.presenters.PlayerPresenter;
import com.zhy.yimalaya.presenters.SubscriptionPresenter;
import com.zhy.yimalaya.utils.ImageBlur;
import com.zhy.yimalaya.utils.LogUtil;
import com.zhy.yimalaya.views.UiLoader;

import java.util.List;

public class DetailActivity extends AppCompatActivity implements IAlbumDetailCallback, UiLoader.OnRefreshListener, TrackListAdapter.OnItemClickListener, IPlayerCallBack, ISubscriptionCallBack {

    private static final String TAG = "DetailActivity";
    public static final int DEALT_PLAY_INDEX = 0;
    public static final String CANCEL_SUBSCRIPTION_TEXT = "取消";
    public static final String SUBSCRIPTION_TEXT = "订阅";
    private ImageView mAlbumCoverSmall;
    private ImageView mAlbumCoverLarge;
    private TextView mAlbumTitle;
    private TextView mAlbumAnnouncerName;
    private IAlbumDetailPresenter mDetailPresenter;
    private FrameLayout mTrackList;
    private TrackListAdapter mTrackListAdapter;
    private UiLoader mUiLoader;
    private View mPlayControlBtn;
    private ImageView mPlayControlIcon;
    private TextView mPlayControlText;
    private PlayerPresenter mPlayerPresenter;
    private TwinklingRefreshLayout mRefreshLayout;
    private SubscriptionPresenter mSubscriptionPresenter;
    private TextView mSubscriptionBtn;

    private Album mCurrentAlbum;
    private boolean hasSub = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCurrentAlbum = extras.getParcelable("album");
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);
        initView();
        setupEvent();
        initPresenter();

    }

    private void initView() {
        mAlbumCoverLarge = findViewById(R.id.album_cover_large);
        mAlbumCoverSmall = findViewById(R.id.album_cover_small);
        mAlbumTitle = findViewById(R.id.album_title);
        mAlbumAnnouncerName = findViewById(R.id.album_announcer_name);
        mTrackList = findViewById(R.id.track_list);
        mUiLoader = new UiLoader(this) {
            @Override
            protected View getSuccessView(LayoutInflater inflater, ViewGroup parent) {
                return createSuccessView(inflater, parent);
            }
        };
        mUiLoader.setOnRefreshClickListener(this);
        mTrackList.addView(mUiLoader);

        mPlayControlBtn = findViewById(R.id.play_control_btn);
        mPlayControlIcon = findViewById(R.id.play_control_icon);
        mPlayControlText = findViewById(R.id.play_control_text);

        mSubscriptionBtn = findViewById(R.id.subscribe);
    }

    private void setupEvent() {

        mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    if (mPlayerPresenter.isPlaying()) {
                        mPlayerPresenter.pause();
                    } else {
                        if (!mPlayerPresenter.hasPlayList()) {
                            mPlayerPresenter.setPlayList(mDetailPresenter.getBufferedList(), DEALT_PLAY_INDEX);
                        }
                        mPlayerPresenter.play();
                    }
                }
            }
        });

        mSubscriptionBtn.setOnClickListener(v -> {
            if (hasSub) {
                mSubscriptionPresenter.remove(mCurrentAlbum);
            } else {
                mSubscriptionPresenter.add(mCurrentAlbum);
            }
        });
    }

    private void setPlayControlPause() {
        mPlayControlIcon.setImageResource(R.drawable.selector_play_pause_black);

        if (mPlayerPresenter != null) {
            mPlayControlText.setText(mPlayerPresenter.getCurrentTrack().getTrackTitle());
        } else {
            mPlayControlText.setText("暂停播放");
        }
        mPlayControlText.setSelected(true);

    }

    private void setPlayControlStart() {
        mPlayControlIcon.setImageResource(R.drawable.selector_play_start_black);
        mPlayControlText.setText("开始播放");
    }

    private void initPresenter() {
        mDetailPresenter = AlbumDetailPresenter.getInstance();
        mDetailPresenter.setTargetAlbum(mCurrentAlbum);
        mDetailPresenter.registerViewCallback(this);

        mDetailPresenter.getAlumDetail();

        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);


        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.isSub(mCurrentAlbum);
    }

    private View createSuccessView(LayoutInflater inflater, final ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_detail_list, parent, false);

        mRefreshLayout = view.findViewById(R.id.detail_list_wrapper);

        mRefreshLayout.setHeaderView(new BezierLayout(parent.getContext()));

        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {

            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                Toast.makeText(parent.getContext(), "onRefresh", Toast.LENGTH_SHORT).show();
                refreshLayout.finishRefreshing();

            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                mDetailPresenter.loadMore();
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.detail_list);
        mTrackListAdapter = new TrackListAdapter();
        mTrackListAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mTrackListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        return view;
    }

    @Override
    public void onTrackListLoaded(TrackList trackList) {
        mUiLoader.updateState(UiLoader.State.SUCCESS);
        mTrackListAdapter.setTrackList(trackList.getTracks());
    }

    @Override
    public void onLoadMore(List<Track> trackList) {
        mTrackListAdapter.setTrackList(trackList);
        mRefreshLayout.finishLoadmore();
        Toast.makeText(this, "加载成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAlbumLoaded(Album album) {
        mAlbumTitle.setText(album.getAlbumTitle());
        mAlbumAnnouncerName.setText(album.getAnnouncer().getNickname());
        Picasso.with(this).load(album.getCoverUrlLarge()).into(mAlbumCoverLarge, new Callback() {
            @Override
            public void onSuccess() {
                ImageBlur.makeBlur(mAlbumCoverLarge, DetailActivity.this);
            }

            @Override
            public void onError() {
                LogUtil.d(TAG, "专辑封面加载失败");
            }
        });
        Picasso.with(this).load(album.getCoverUrlLarge()).into(mAlbumCoverSmall);
    }

    @Override
    public void onNetworkError() {
        mUiLoader.updateState(UiLoader.State.NETWORK_ERROR);
    }

    @Override
    public void beforeRequest() {
        mUiLoader.updateState(UiLoader.State.LOADING);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDetailPresenter.unregisterViewCallback(this);
        mPlayerPresenter.unregisterViewCallback(this);
    }

    @Override
    public void onRefresh() {
        mDetailPresenter.getAlumDetail();
    }

    @Override
    public void onItemClicked(int index, Track track) {
        PlayerPresenter.getInstance().setPlayList(mDetailPresenter.getBufferedList(), index);
        Toast.makeText(this, String.valueOf(index), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, PlayerActivity.class));
    }

    @Override
    public void onSoundPrepared() {

    }

    @Override
    public void onPlayStarted() {
        setPlayControlPause();
    }

    @Override
    public void onPlayPaused() {
        setPlayControlStart();
    }

    @Override
    public void onListRefreshed(List<Track> list) {

    }

    @Override
    public void onPlayModeChanged(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void onPlayProgressChanged(int current, int duration) {

    }

    @Override
    public void onTrackSwitched(Track track) {

    }

    @Override
    public void onOrderChanged(boolean isReverse, int index) {

    }

    //---------- SubscriptionPresenter 结果回调 ---------
    @Override
    public void onAddResult(boolean success) {
        if (success) {
            updateSubscriptionBtnState(true);
        } else {
            Toast.makeText(this, "订阅失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveResult(boolean success) {
        if (success) {
            updateSubscriptionBtnState(false);
        } else {
            Toast.makeText(this, "取消订阅失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaded(List<Album> albums) {

    }


    @Override
    public void onSubCheckResult(boolean sub) {
        updateSubscriptionBtnState(sub);
    }

    @Override
    public void onSubFull() {
        Toast.makeText(this, "订阅已满", Toast.LENGTH_SHORT).show();
    }


    private void updateSubscriptionBtnState(boolean sub) {
        hasSub = sub;
        mSubscriptionBtn.setText(sub ? CANCEL_SUBSCRIPTION_TEXT : SUBSCRIPTION_TEXT);
    }
}
