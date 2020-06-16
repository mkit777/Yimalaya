package com.zhy.yimalaya.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.zhy.yimalaya.utils.LogUtil;

public class BaseApplication extends Application {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }


    public static final Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        CommonRequest mXimalaya = CommonRequest.getInstanse();

        String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
        mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
        mXimalaya.setPackid("com.app.test.android");
        mXimalaya.init(this, mAppSecret);

        // 初始化日志工具类
        LogUtil.init("Zimalaya", false);

        // 初始化播放器
        XmPlayerManager.getInstance(this).init();
        sContext = getApplicationContext();

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        XmPlayerManager.release();
    }
}
