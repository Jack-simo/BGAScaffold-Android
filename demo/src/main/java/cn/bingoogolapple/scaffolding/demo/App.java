package cn.bingoogolapple.scaffolding.demo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import cn.bingoogolapple.scaffolding.demo.hyphenatechat.EmUtil;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.HttpRequestException;
import cn.bingoogolapple.scaffolding.util.RxBus;
import cn.bingoogolapple.scaffolding.util.RxEvent;
import cn.bingoogolapple.scaffolding.util.UmengUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午4:13
 * 描述:
 */
public class App extends Application {
    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        if (AppManager.isInOtherProcess(this)) {
            Log.e("App", "enter the other process!");
            return;
        }

        // 初始化内存泄露检测库
        mRefWatcher = LeakCanary.install(this);
        // 初始化应用程序管理器
        AppManager.getInstance().init(BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"), new AppManager.Delegate() {
            @Override
            public void refWatcherWatchFragment(Fragment fragment) {
                mRefWatcher.watch(fragment);
            }

            @Override
            public void handleServerException(HttpRequestException httpRequestException) {
                Logger.i("处理网络请求异常");
            }
        });

        RxBus.toObservable(RxEvent.AppEnterForegroundEvent.class).subscribe(appEnterForegroundEvent -> {
            Logger.i("应用进入前台");
        });
        RxBus.toObservable(RxEvent.AppEnterBackgroundEvent.class).subscribe(appEnterBackgroundEvent -> {
            Logger.i("应用进入后台");
        });

        // 初始化友盟 SDK
        UmengUtil.initSdk();

        // 初始化环信 SDK
        EmUtil.initSdk();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}