package com.zhy.yimalaya.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhy.yimalaya.R;

import java.util.ArrayList;
import java.util.List;

public class FlowView extends ViewGroup {

    private static final int DEFAULT_HORIZONTAL_MARGIN = 10;
    private static final int DEFAULT_VERTICAL_MARGIN = 10;
    private static final int DEFAULT_HORIZONTAL_PADDING = 20;
    private static final int DEFAULT_VERTICAL_PADDING = 20;
    private static final int DEFAULT_MAX_LINES = -1;


    private int mHorizontalMargin;
    private int mVerticalMargin;
    private int mHorizontalPadding;
    private int mVerticalPadding;
    private int mMaxLines;


    // 2. 实现构造方法
    public FlowView(Context context) {
        this(context, null);
    }

    public FlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        // 3. 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowView);

        mHorizontalMargin = (int) typedArray.getDimension(R.styleable.FlowView_horizontalMargin, DEFAULT_HORIZONTAL_MARGIN);
        mVerticalMargin = (int) typedArray.getDimension(R.styleable.FlowView_verticalMargin, DEFAULT_VERTICAL_MARGIN);
        mHorizontalPadding = (int) typedArray.getDimension(R.styleable.FlowView_horizontalPadding, DEFAULT_HORIZONTAL_PADDING);
        mVerticalPadding = (int) typedArray.getDimension(R.styleable.FlowView_verticalPadding, DEFAULT_VERTICAL_PADDING);
        mMaxLines = typedArray.getInt(R.styleable.FlowView_maxLines, DEFAULT_MAX_LINES);
        // 注意释放资源
        typedArray.recycle();
    }


    private List<String> mData = new ArrayList<>();

    public void setData(List<String> data) {
        // 缓存当前的容器中的View
        mData.clear();
        mData.addAll(data);

        // 将子View添加到当前视图中
        removeAllViews();
        for (String str : mData) {
            addView(createItem(str));
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private TextView createItem(String text) {
        final TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_flow, this, false);
        view.setText(text);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(view);
                }
            }
        });
        return view;
    }


    // 4. 测量子View，并确定当前View的宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 当前元素实际的宽高
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 减去padding后可用于布局子View的宽高
        int contentWidth = parentWidth - mHorizontalPadding * 2;
        int contentHeight = parentHeight - mVerticalPadding * 2;

        int childWidthSpace = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.AT_MOST);
        int childHeightSpace = MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.AT_MOST);

        int lineWidthAcc = 0;
        int lineHeightAcc = 0;

        int maxHeight = 0;
        int itemCounter = 0;
        int lineCounter = 1;

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            measureChild(childView, childWidthSpace, childHeightSpace);

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            if (itemCounter == 0 || lineWidthAcc + childWidth + mHorizontalMargin * itemCounter <= contentWidth) {
                // 可以放置于一行中
                lineWidthAcc += childWidth;
                maxHeight = childHeight;
                itemCounter++;
            } else {
                if (mMaxLines != -1 && lineCounter == mMaxLines) {
                    // 已经达到最大行，放弃后续 View的布局
                    break;
                }
                // 需要另起一行
                lineWidthAcc = childWidth;
                lineHeightAcc += maxHeight;
                maxHeight = childHeight;
                itemCounter = 1;
                lineCounter++;
            }
        }
        lineHeightAcc += maxHeight + mVerticalMargin * (lineCounter - 1) + mVerticalPadding * 2;
        setMeasuredDimension(parentWidth, lineHeightAcc);
    }

    // 5. 重写
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cL = mHorizontalPadding;
        int cT = mVerticalPadding;

        int lineItemCounter = 0;
        int maxHeightOfLine = 0;

        int lineCounter = 1;

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            // 获取子元素的宽高
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();


            if (lineItemCounter == 0 || cL + childWidth <= r - mHorizontalPadding) {
                childView.layout(cL, cT, cL + childWidth, cT + childHeight);
                cL += childWidth + mHorizontalMargin;
                lineItemCounter++;
                maxHeightOfLine = Math.max(maxHeightOfLine, childHeight);
            } else {
                if (mMaxLines != -1 && lineCounter == mMaxLines) {
                    break;
                }

                cL = l + mHorizontalPadding;
                cT += maxHeightOfLine + mVerticalMargin;

                childView.layout(cL, cT, cL + childWidth, cT + childHeight);

                cL += childWidth + mHorizontalMargin;
                lineItemCounter = 1;
                maxHeightOfLine = childHeight;

                lineCounter++;
            }
        }
    }


    public interface OnItemClickListener {
        void onItemClicked(TextView view);
    }

}