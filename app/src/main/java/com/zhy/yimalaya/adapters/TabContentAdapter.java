package com.zhy.yimalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.List;

/**
 * 导航条的适配器
 */
public class TabContentAdapter extends CommonNavigatorAdapter {

    private List<String> mDataList;
    private ViewPager mViewPager;

    public TabContentAdapter(List<String> data, ViewPager viewPager) {
        this.mDataList = data;
        this.mViewPager = viewPager;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }


    /**
     * 指定 Tab 标签如何显示
     */
    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
        colorTransitionPagerTitleView.setNormalColor(Color.LTGRAY);
        colorTransitionPagerTitleView.setSelectedColor(Color.WHITE);
        colorTransitionPagerTitleView.setTextSize(16);

        colorTransitionPagerTitleView.setText(mDataList.get(index));
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(index);
            }
        });
        return colorTransitionPagerTitleView;
    }

    /**
     * 指定 Tab 滑块如何展示
     */
    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setColors(Color.WHITE);
        indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        return indicator;
    }
}
