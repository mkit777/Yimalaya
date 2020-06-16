package com.zhy.yimalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.zhy.yimalaya.adapters.PlayListAdapter;
import com.zhy.yimalaya.adapters.PlayerPagerAdapter;
import com.zhy.yimalaya.interfaces.IPlayerCallBack;
import com.zhy.yimalaya.interfaces.IPlayerPresenter;
import com.zhy.yimalaya.presenters.PlayerPresenter;
import com.zhy.yimalaya.strategy.PlayModeMachine;
import com.zhy.yimalaya.views.PlayListPopWindow;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlayerActivity extends AppCompatActivity implements IPlayerCallBack, PlayListAdapter.ClickListener, PlayListPopWindow.EventListener {

    private static final String TAG = "PlayerActivity";
    public static final String INIT_TIME_STATE = "00:00";
    IPlayerPresenter mPlayerPresenter;
    private TextView mTrackTitle;
    private TextView mProgressCurrent;
    private SeekBar mProgress;
    private TextView mProgressDuration;
    private ImageView mPlayerPrevBtn;
    private ImageView mPlayerStopBtn;
    private ImageView mPlayerNextBtn;
    private ImageView mPlayerListBtn;
    private DateTimeFormatter mTimeFormatter = DateTimeFormatter.ofPattern("mm:ss");
    private ViewPager mTrackAlbumPager;
    private PlayerPagerAdapter mTrackAlbumAdapter;
    private ImageView mPlayerModeBtn;
    private PlayListPopWindow mPlayListPopWindow;
    private ValueAnimator mWindowEnterValueAnimator;
    private ValueAnimator mWindowExitValueAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏模式
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);
        // 初始视图
        initView();
        // 为视图绑定事件
        setupEvent();
        // 视图初始化完毕
        initPresenter();
    }


    /**
     * 初始化视图
     */
    private void initView() {
        mTrackTitle = findViewById(R.id.play_track_title);

        mTrackAlbumPager = findViewById(R.id.play_image_list);
        mTrackAlbumAdapter = new PlayerPagerAdapter();
        mTrackAlbumPager.setAdapter(mTrackAlbumAdapter);

        mProgress = findViewById(R.id.player_progress);
        mProgressCurrent = findViewById(R.id.player_progress_current);
        mProgressDuration = findViewById(R.id.player_progress_duration);


        mPlayerModeBtn = findViewById(R.id.player_mode_btn);
        mPlayerPrevBtn = findViewById(R.id.player_prev_btn);
        mPlayerStopBtn = findViewById(R.id.player_stop_btn);
        mPlayerNextBtn = findViewById(R.id.player_next_btn);
        mPlayerListBtn = findViewById(R.id.player_list_btn);

        mPlayListPopWindow = new PlayListPopWindow(this);
        initAnimator();
    }

    /**
     * 初始化事件
     */
    private void setupEvent() {

        mTrackAlbumPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mPlayerPresenter != null) {
                    if (!isUpdatePager) {
                        mPlayerPresenter.playByIndex(position);
                    }
                }
                isUpdatePager = false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.seekTo(seekBar.getProgress());
                }
            }
        });


        mPlayerModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToNextMode();
            }
        });


        mPlayerPrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPrev();
                }
            }
        });

        mPlayerStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    if (!mPlayerPresenter.isPlaying()) {
                        mPlayerPresenter.play();
                    } else {
                        mPlayerPresenter.pause();
                    }
                }
            }
        });

        mPlayerNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });

        mPlayerListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayListPopWindow.showAtBottom(v, mPlayerPresenter.getCurrentIndex());
                mWindowEnterValueAnimator.start();
            }
        });

        mPlayListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mWindowExitValueAnimator.start();
            }
        });

        mPlayListPopWindow.setEventListener(this);
    }


    private void initPresenter() {
        // 绑定播放器
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
    }

    /**
     * 更新上一曲、下一曲按钮的状态
     */
    private void updatePlayButton() {
        if (mPlayerPresenter != null) {
            mPlayerPrevBtn.setEnabled(true);
            mPlayerNextBtn.setEnabled(true);
            if (mPlayerPresenter.isFirst()) {
                mPlayerPrevBtn.setEnabled(false);
            }
            if (mPlayerPresenter.isLast()) {
                mPlayerNextBtn.setEnabled(false);
            }
        }
    }

    private void initAnimator() {
        mWindowEnterValueAnimator = ValueAnimator.ofFloat(1, 0.4f);
        mWindowEnterValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateWindowAlpha(((float) animation.getAnimatedValue()));
            }
        });

        mWindowExitValueAnimator = ValueAnimator.ofFloat(0.4f, 1);
        mWindowExitValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateWindowAlpha(((float) animation.getAnimatedValue()));
            }
        });
    }

    private void updateWindowAlpha(float alpha) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = alpha;
        getWindow().setAttributes(attributes);
    }


    /**
     * 音频准备就绪，可以播放音频
     */
    @Override
    public void onSoundPrepared() {
        mPlayerPresenter.play();
    }

    /**
     * 开始播放
     */
    @Override
    public void onPlayStarted() {
        mPlayerStopBtn.setImageResource(R.drawable.selector_stop);
    }

    /**
     * 暂停播放
     */
    @Override
    public void onPlayPaused() {
        mPlayerStopBtn.setImageResource(R.drawable.selector_play);
    }

    /**
     * 播放列表更新
     */
    @Override
    public void onListRefreshed(List<Track> list) {
        mTrackAlbumAdapter.updateData(list);
        mPlayListPopWindow.updateData(list);
    }

    /**
     * 播放模式改变
     */
    @Override
    public void onPlayModeChanged(XmPlayListControl.PlayMode mode) {
        PlayModeMachine.ModeInfo modeInfo = PlayModeMachine.getModeInfo(mode);
        mPlayerModeBtn.setImageResource(modeInfo.getImg());
        mPlayListPopWindow.updatePlayMode(modeInfo.getImg(), modeInfo.getText());
    }

    /**
     * 播放进度改变
     */
    @Override
    public void onPlayProgressChanged(int current, int duration) {
        mProgressCurrent.setText(formatTime(current));
        mProgressDuration.setText(formatTime(duration));
        mProgress.setMax(duration);
        if (!mProgress.isPressed()) {
            mProgress.setProgress(current, false);
        }
    }

    private String formatTime(int time) {
        return LocalTime.ofSecondOfDay(time / 1000).format(mTimeFormatter);
    }


    private boolean isUpdatePager = false;

    /**
     * 音频切换
     */
    @Override
    public void onTrackSwitched(Track newTrack) {
        if (!mPlayerPresenter.isPlaying()) {
            mPlayerPresenter.play();
        }

        int index = mPlayerPresenter.getCurrentIndex();
        // 音乐切换有两种方式，一种是通过上一首、下一首等按钮来切换，另一种是通过图像列表切换
        // 而且前者切换方式需要同步更新图像列表的位置
        // 当图像列表中展示的音频是与当前音频不匹配，即为前一种切换方式，需要进行同步
        if (index != mTrackAlbumPager.getCurrentItem()) {
            // 同步需要一个标志位，防止递归
            isUpdatePager = true;
            mTrackAlbumPager.setCurrentItem(index, true);
        }
        mTrackTitle.setText(newTrack.getTrackTitle());
        initProgress();
        updatePlayButton();
        // 更新播放列表中的焦点项
        mPlayListPopWindow.updateFocusItem(index);
    }

    /**
     * 初始化进度条的展示
     */
    private void initProgress() {
        mProgressCurrent.setText(INIT_TIME_STATE);
        mProgressDuration.setText(INIT_TIME_STATE);
        mProgress.setProgress(0);
        mProgress.setSecondaryProgress(0);
    }

    /**
     * 播放顺序改变
     */
    @Override
    public void onOrderChanged(boolean isReverse, int index) {
        mPlayListPopWindow.updatePlayOrder(isReverse);
        mPlayListPopWindow.updateFocusItem(index);
    }

    /**
     * 播放列表中的某项被点击
     */
    @Override
    public void onPlayListItemClicked(int index) {
        mPlayerPresenter.playByIndex(index);
    }

    /**
     * 弹出窗中的模式切换按钮被点击
     */
    @Override
    public void onModeBtnClicked() {
        switchToNextMode();
    }

    private void switchToNextMode() {
        XmPlayListControl.PlayMode currentMode = mPlayerPresenter.getCurrentMode();
        mPlayerPresenter.switchPlayMode(PlayModeMachine.nextMode(currentMode));
    }

    /**
     * 顺序切换按钮被点击
     */
    @Override
    public void onOrderBtnClicked() {
        mPlayerPresenter.switchOrder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unregisterViewCallback(this);
        }
    }
}
