package cn.bingoogolapple.basenote;

import android.app.Application;
import android.app.Notification;
import android.support.v4.app.NotificationManagerCompat;

import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zhy.changeskin.SkinManager;

import cn.bingoogolapple.basenote.util.AppManager;
import cn.bingoogolapple.basenote.util.CrashHandler;

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
        sInstance = this;

        mRefWatcher = LeakCanary.install(this);
        SkinManager.getInstance().init(this);
        initAppManager();
        CrashHandler.getInstance().init(this);

        mNotificationManager = NotificationManagerCompat.from(this);

        Logger.init().methodCount(3).hideThreadInfo().logLevel(LogLevel.FULL).methodOffset(2).logTool(new AndroidLogTool());
    }

    public static App getInstance() {
        return sInstance;
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    private void initAppManager() {
        AppManager.getInstance().init(this).setDelegate(new AppManager.Delegate() {
            @Override
            public void onEnterFrontStage() {
                Logger.i(TAG, "进入前台状态");
            }

            @Override
            public void onEnterBackStage() {
                Logger.i(TAG, "进入后台状态");
            }
        });
        registerActivityLifecycleCallbacks(AppManager.getInstance());
    }

    public void addNotification(int id, Notification notification) {
        mNotificationManager.notify(id, notification);
    }

    public void removeNotification(int id) {
        mNotificationManager.cancel(id);
    }
}