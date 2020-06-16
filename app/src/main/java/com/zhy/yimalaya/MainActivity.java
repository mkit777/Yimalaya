package com.zhy.yimalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.zhy.yimalaya.adapters.MainContentAdapter;
import com.zhy.yimalaya.adapters.TabContentAdapter;
import com.zhy.yimalaya.api.XimalayaApi;
import com.zhy.yimalaya.interfaces.IPlayerCallBack;
import com.zhy.yimalaya.interfaces.IPlayerPresenter;
import com.zhy.yimalaya.presenters.PlayerPresenter;
import com.zhy.yimalaya.presenters.RecommendPresenter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IPlayerCallBack {
    public static final String TAG = "MainActivity";


    private ViewPager mViewPager;
    private MagicIndicator mMagicIndicator;

    private List<String> mDataList;
    private IPlayerPresenter mPlayerPresenter;
    private ImageView mPlayerAlbum;
    private TextView mPlayerTrackTitle;
    private TextView mPlayerTrackAuthor;
    private ImageView mPlayerControlBtn;
    private View mPlayer;
    private ImageView mSearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary, getTheme()), 0);
        initView();
        setupEvent();
        initPresenter();
    }


    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
    }


    private void initView() {
        mViewPager = findViewById(R.id.view_pager);
        mMagicIndicator = findViewById(R.id.magic_indicator);

        mViewPager.setAdapter(new MainContentAdapter(getSupportFragmentManager()));

        // 创建导航栏
        CommonNavigator navigator = new CommonNavigator(this);
        // 自适应宽度
        navigator.setAdjustMode(true);
        // 设置导航背景
        navigator.setBackgroundColor(getResources().getColor(R.color.colorPrimary, getTheme()));

        String[] tabTitles = getResources().getStringArray(R.array.tab_titles);
        mDataList = Arrays.asList(tabTitles);

        // 设置导航适配器
        navigator.setAdapter(new TabContentAdapter(mDataList, mViewPager));
        // 将导航绑定到指示器上
        mMagicIndicator.setNavigator(navigator);
        // 将指示器与视图页绑定
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);


        mPlayerAlbum = findViewById(R.id.track_album);
        mPlayerTrackTitle = findViewById(R.id.track_title);
        mPlayerTrackAuthor = findViewById(R.id.track_author);
        mPlayerControlBtn = findViewById(R.id.play_control_btn);
        mPlayer = findViewById(R.id.player);

        mSearchBtn = findViewById(R.id.search_btn);

    }

    private void setupEvent() {
        mPlayerControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                } else {
                    if (!mPlayerPresenter.hasPlayList()) {
                        playFirstRecommend();
                    } else {
                        mPlayerPresenter.play();
                    }
                }
            }
        });

        mPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter.hasPlayList()) {
                    startActivity(new Intent(MainActivity.this, PlayerActivity.class));
                }
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
    }

    private void playFirstRecommend() {
        Album album = RecommendPresenter.getInstance().getCurrentRecommendList().get(0);

        XimalayaApi.getInstance().getTrackDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                mPlayerPresenter.setPlayList(trackList.getTracks(), 0);
            }

            @Override
            public void onError(int i, String s) {

            }
        }, album.getId(), 1);

    }

    @Override
    public void onSoundPrepared() {
        mPlayerPresenter.play();
    }

    @Override
    public void onPlayStarted() {
        mPlayerControlBtn.setImageResource(R.drawable.selector_stop);
        mPlayerTrackTitle.setSelected(true);
    }

    @Override
    public void onPlayPaused() {
        mPlayerControlBtn.setImageResource(R.drawable.selector_play);
        mPlayerTrackTitle.setSelected(false);

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
        if (track != null) {
            Picasso.with(this).load(track.getAlbum().getCoverUrlLarge()).into(mPlayerAlbum);
            mPlayerTrackTitle.setText(track.getTrackTitle());
            mPlayerTrackTitle.setSelected(true);
            mPlayerTrackAuthor.setText(track.getAnnouncer().getNickname());
        }
    }

    @Override
    public void onOrderChanged(boolean isReverse, int index) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerPresenter.unregisterViewCallback(this);
    }
}
