package com.zhy.yimalaya.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.zhy.yimalaya.R;
import com.zhy.yimalaya.adapters.PlayListAdapter;

import java.util.List;


public class PlayListPopWindow extends PopupWindow {

    private TextView mCloseBtn;
    private RecyclerView mListRecyclerView;
    private PlayListAdapter mListAdapter;
    private View mPlayModeBtn;
    private ImageView mPlayModeIcon;
    private TextView mPlayModeText;
    private EventListener mEventListener;
    private View mOrderBtn;
    private ImageView mOrderIcon;
    private TextView mOrderText;


    public PlayListPopWindow(Context context) {
        // 设置弹窗的宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initView(context);
        setupEvent();

    }


    private void initView(Context context) {
        // 加载视图
        View view = LayoutInflater.from(context).inflate(R.layout.view_play_list, null);
        setContentView(view);
        // 设置动画
        setAnimationStyle(R.style.PlayListAnimation);

        mPlayModeBtn = view.findViewById(R.id.player_mode_btn);
        mPlayModeIcon = view.findViewById(R.id.play_mode_icon);
        mPlayModeText = view.findViewById(R.id.play_mode_text);

        mOrderBtn = view.findViewById(R.id.play_order_btn);
        mOrderIcon = view.findViewById(R.id.play_order_icon);
        mOrderText = view.findViewById(R.id.play_order_text);

        mListRecyclerView = view.findViewById(R.id.play_list);
        mListRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        mListAdapter = new PlayListAdapter();
        mListRecyclerView.setAdapter(mListAdapter);

        mCloseBtn = view.findViewById(R.id.close_btn);
    }


    private void setupEvent() {
        // 点击弹窗外部直接关闭弹窗
        setOutsideTouchable(true);

        // 点击弹窗中的关闭按钮关闭弹窗
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mPlayModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener != null) {
                    mEventListener.onModeBtnClicked();
                }
            }
        });

        mOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventListener != null) {
                    mEventListener.onOrderBtnClicked();
                }
            }
        });

        mListAdapter.setClickListener(new PlayListAdapter.ClickListener() {
            @Override
            public void onPlayListItemClicked(int index) {
                if (mEventListener != null) {
                    mEventListener.onPlayListItemClicked(index);
                }
            }
        });
    }

    public void showAtBottom(View view, int focusIndex) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
        updateFocusItem(focusIndex);
    }


    public void updatePlayMode(int icon, String text) {
        mPlayModeIcon.setImageResource(icon);
        mPlayModeText.setText(text);
    }

    public void updatePlayOrder(boolean isReverse) {
        if (isReverse) {
            mOrderIcon.setImageResource(R.drawable.selector_sort_asc);
            mOrderText.setText("倒序");
        } else {
            mOrderIcon.setImageResource(R.drawable.selector_sort_desc);
            mOrderText.setText("升序");
        }
    }

    public void updateData(List<Track> list) {
        mListAdapter.setData(list);
    }


    public void updateFocusItem(int index) {
        mListAdapter.setSelectedIndex(index);
        mListRecyclerView.smoothScrollToPosition(index);
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public interface EventListener {
        void onModeBtnClicked();

        void onOrderBtnClicked();

        void onPlayListItemClicked(int index);
    }
}
