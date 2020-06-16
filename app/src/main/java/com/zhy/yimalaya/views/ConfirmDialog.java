package com.zhy.yimalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhy.yimalaya.R;

public class ConfirmDialog extends Dialog {

    private TextView mConfirmText;
    private TextView mCancelBtn;
    private TextView mConfirmBtn;
    private OnConfirmListener mOnConfirmListener;

    public ConfirmDialog(@NonNull Context context) {
        this(context, 0);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        this(context, true, null);
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.view_subcription_dialog);
        initView();
        initListener();
    }

    private void initView() {
        mConfirmText = findViewById(R.id.confirm_text);
        mCancelBtn = findViewById(R.id.cancel_btn);
        mConfirmBtn = findViewById(R.id.confirm_btn);
    }

    private void initListener() {
        mCancelBtn.setOnClickListener(v -> {
            if (mOnConfirmListener != null) {
                mOnConfirmListener.onConfirm(false);
                dismiss();
            }
        });

        mConfirmBtn.setOnClickListener(v -> {
            if (mOnConfirmListener != null) {
                mOnConfirmListener.onConfirm(true);
                dismiss();
            }
        });
    }


    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        mOnConfirmListener = onConfirmListener;
    }

    public interface OnConfirmListener {
        void onConfirm(boolean confirm);
    }

}
