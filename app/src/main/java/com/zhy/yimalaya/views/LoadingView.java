package com.zhy.yimalaya.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.zhy.yimalaya.R;
import com.zhy.yimalaya.utils.LogUtil;


public class LoadingView extends androidx.appcompat.widget.AppCompatImageView {
    public static final String TAG = "LoadingView";
    private boolean rotating = false;
    private int degree = 0;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.loading);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtil.d(TAG,"开始旋转");
        rotating = true;
        startRotating();
    }

    private void startRotating() {
        post(new Runnable() {
            @Override
            public void run() {
                if (!rotating) {
                    LogUtil.d(TAG,"停止旋转");
                    return;
                }
                degree += 30;
                degree %= 360;

                invalidate();
                postDelayed(this, 50);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        rotating = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(degree, getHeight() / 2, getWidth() / 2);
        super.onDraw(canvas);
    }
}
