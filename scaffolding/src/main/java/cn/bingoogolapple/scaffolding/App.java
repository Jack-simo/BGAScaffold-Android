package cn.bingoogolapple.scaffolding;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationManagerCompat;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import cn.bingoogolapple.scaffolding.util.AppManager;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午4:13
 * 描述:
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    private static App sInstance;
    private NotificationManagerCompat mNotificationManager;
    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        // 开发环境相关 START
        Logger.init("BGANote").logLevel(LogLevel.FULL);

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            Logger.d("In LeakCanary Analyzer Process");
//            return;
//        }
        mRefWatcher = LeakCanary.install(this);
        // 开发环境相关 END


        sInstance = this;
        registerActivityLifecycleCallbacks(AppManager.getInstance().init(this));
        mNotificationManager = NotificationManagerCompat.from(this);
    }

    public static App getInstance() {
        return sInstance;
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    public void addNotification(int id, Notification notification) {
        mNotificationManager.notify(id, notification);
    }

    public void removeNotification(int id) {
        mNotificationManager.cancel(id);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}