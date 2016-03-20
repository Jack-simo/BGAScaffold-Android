package cn.bingoogolapple.basenote.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Stack;

import cn.bingoogolapple.basenote.App;
import cn.bingoogolapple.basenote.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/21 上午1:25
 * 描述:
 */
public class AppManager implements Application.ActivityLifecycleCallbacks {
    private int mActivityStartedCount = 0;
    private long mLastPressBackKeyTime;
    private Stack<Activity> mActivityStack = new Stack<>();
    private Delegate mDelegate;

    public AppManager(Delegate delegate) {
        mDelegate = delegate;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityStack.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (mActivityStartedCount == 0 && mDelegate != null) {
            mDelegate.onEnterFrontStage();
        }
        mActivityStartedCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityStartedCount--;
        if (mActivityStartedCount == 0 && mDelegate != null) {
            mDelegate.onEnterBackStage();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityStack.remove(activity);
    }

    public Activity currentActivity() {
        Activity activity = null;
        if (!mActivityStack.empty()) {
            activity = mActivityStack.lastElement();
        }
        return activity;
    }

    public void popOneActivity(Activity activity) {
        if (activity == null || mActivityStack.isEmpty()) {
            return;
        }
        if (!activity.isFinishing()) {
            activity.finish();
        }
        mActivityStack.remove(activity);
    }

    /**
     * 双击后 全退出应用程序
     */
    public void exitWithDoubleClick() {
        if (System.currentTimeMillis() - mLastPressBackKeyTime <= 1500) {
            exit();
        } else {
            mLastPressBackKeyTime = System.currentTimeMillis();
            ToastUtil.show(R.string.toast_exit_tip);
        }
    }

    /**
     * 退出应用程序
     */
    public void exit() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popOneActivity(activity);
        }
        System.gc();
    }

    /**
     * 获取当前版本名称
     *
     * @return
     */
    public String getCurrentVersionName() {
        try {
            return App.getInstance().getPackageManager().getPackageInfo(App.getInstance().getPackageName(), 0).versionName;
        } catch (Exception e) {
            // 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
            return null;
        }
    }

    public interface Delegate {
        void onEnterFrontStage();

        void onEnterBackStage();
    }
}