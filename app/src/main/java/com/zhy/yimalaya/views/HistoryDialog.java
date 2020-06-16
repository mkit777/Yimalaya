package com.zhy.yimalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhy.yimalaya.R;

public class HistoryDialog extends Dialog {

    private CheckBox mClearAll;
    private TextView mConfirmBtn;
    private TextView mCancelBtn;
    private OnDialogConfirmed mOnConfirmListener;

    public HistoryDialog(@NonNull Context context) {
        this(context, 0);
    }

    public HistoryDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected HistoryDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_history_dialog);
        initView();
        setupEvent();
    }

    private void setupEvent() {
        mConfirmBtn.setOnClickListener(v -> {
            if (mOnConfirmListener != null) {
                mOnConfirmListener.onConfirmed(mClearAll.isChecked(),true);
            }
            dismiss();
        });

        mCancelBtn.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void initView() {
        mClearAll = findViewById(R.id.clearAll);
        mConfirmBtn = findViewById(R.id.confirm_btn);
        mCancelBtn = findViewById(R.id.cancel_btn);
    }

    public interface OnDialogConfirmed {
        void onConfirmed(boolean clearAll, boolean confirm);
    }

    public void setOnConfirmListener(OnDialogConfirmed onConfirmListener) {
        mOnConfirmListener = onConfirmListener;
    }
}
