package cn.bingoogolapple.basenote;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationManagerCompat;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import cn.bingoogolapple.basenote.util.AppManager;

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

        registerActivityLifecycleCallbacks(AppManager.getInstance().init(this));

        mRefWatcher = LeakCanary.install(this);
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