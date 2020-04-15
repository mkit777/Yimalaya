package com.zhy.yimalaya.base;

import android.app.Application;
import android.os.Handler;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.zhy.yimalaya.utils.LogUtil;

public class BaseApplication extends Application {

    public static Handler handler = new Handler();
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
    }
}
