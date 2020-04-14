package com.zhy.yimalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;


import com.zhy.yimalaya.adapters.MainContentAdapter;
import com.zhy.yimalaya.adapters.TabContentAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";


    private ViewPager mViewPager;
    private MagicIndicator mMagicIndicator;

    private List<String> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadTabData();
        initView();
    }

    private void loadTabData() {
        String[] tabTitles = getResources().getStringArray(R.array.tab_titles);
        mDataList = Arrays.asList(tabTitles);
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
        // 设置导航适配器
        navigator.setAdapter(new TabContentAdapter(mDataList, mViewPager ));
        // 将导航绑定到指示器上
        mMagicIndicator.setNavigator(navigator);
        // 将指示器与视图页绑定
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }
}
