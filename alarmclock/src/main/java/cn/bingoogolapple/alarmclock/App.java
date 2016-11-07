package cn.bingoogolapple.alarmclock;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;

import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.RxBus;
import cn.bingoogolapple.scaffolding.util.RxEvent;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午4:13
 * 描述:
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            Log.d("LeakCanary", "In LeakCanary Analyzer Process");
            return;
        }

        AppManager.init(BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"));

        RxBus.toObservable(RxEvent.AppEnterForegroundEvent.class).subscribe(appEnterForegroundEvent -> {
            Logger.i("应用进入前台");
        });
        RxBus.toObservable(RxEvent.AppEnterBackgroundEvent.class).subscribe(appEnterBackgroundEvent -> {
            Logger.i("应用进入后台");
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}