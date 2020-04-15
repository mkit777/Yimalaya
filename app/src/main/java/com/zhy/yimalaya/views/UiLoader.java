package com.zhy.yimalaya.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.zhy.yimalaya.R;
import com.zhy.yimalaya.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class UiLoader extends FrameLayout {
    public interface OnRefreshListener {
        void onRefresh();
    }

    public enum State {
        NONE, LOADING, SUCCESS, EMPTY, NETWORK_ERROR
    }

    public static final String TAG = "UiLoader";

    private Map<State, View> viewMap = new HashMap<>(5);
    private State state = State.NONE;
    private OnRefreshListener onRefreshClickListener;

    public UiLoader(@NonNull Context context) {
        super(context);
    }

    public void updateState(final State state) {
        updateView(this.state, state);
        this.state = state;

    }

    private void updateView(State oldState, State newState) {
        View oldView = viewMap.get(oldState);
        if (oldView != null) {
            oldView.setVisibility(GONE);
            LogUtil.d(TAG, "隐藏视图 state=" + oldState.name());
        }

        View newView = viewMap.get(newState);
        if (newView == null) {
            newView = createViewByState(newState);
            viewMap.put(newState, newView);
        }
        LogUtil.d(TAG, "显示视图 state=" + newState.name());
        newView.setVisibility(VISIBLE);
    }

    private View createViewByState(State newState) {
        View view = null;
        switch (newState) {
            case LOADING:
                view = getLoadingView();
                break;
            case SUCCESS:
                view = getSuccessView(LayoutInflater.from(getContext()), this);
                break;
            case EMPTY:
                view = getEmptyView();
                break;
            case NETWORK_ERROR:
                view = geErrorView();
                break;
        }
        addView(view);
        return view;
    }

    private View geErrorView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_error, this, false);
        view.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRefreshClickListener != null) {
                    onRefreshClickListener.onRefresh();
                }
            }
        });
        return view;
    }

    private View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_empty, this, false);
    }

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_loading, this, false);
    }

    public void setOnRefreshClickListener(OnRefreshListener onRefreshClickListener) {
        this.onRefreshClickListener = onRefreshClickListener;
    }

    protected abstract View getSuccessView(LayoutInflater inflater, ViewGroup parent);
}
