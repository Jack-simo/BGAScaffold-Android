package cn.bingoogolapple.basenote.util;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

import cn.bingoogolapple.basenote.App;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/3 下午11:13
 * 描述:友盟 SDK 工具类
 */
public class UmengUtil {
    private UmengUtil() {
    }

    /**
     * 初始化友盟 SDK，在 Application 的 onCreate 方法里调用
     */
    public static void initSdk() {
        MobclickAgent.setDebugMode(EnvironmentUtil.isBuildDebug());
        // 禁止默认的页面统计方式，这样将不会再自动统计Activity
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setSessionContinueMillis(EnvironmentUtil.isBuildDebug() ? 3000 : 30000);
    }

    /**
     * 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用MobclickAgent.onKillProcess(Context context)方法，用来保存统计数据
     */
    public static void onKillProcess() {
        MobclickAgent.onKillProcess(App.getInstance());
    }

    // ======================== 页面路径统计 START ========================

    // ======================== Activity START ========================

    /**
     * Activity 中是否包含 Fragment
     *
     * @param activity
     * @return
     */
    private static boolean isActivityNotContainFragment(Activity activity) {
        return true;
    }

    /**
     * 在 ActivityLifecycleCallbacks 的 onActivityResumed 回调方法里调用
     *
     * @param activity
     */
    public static void onActivityResumed(Activity activity) {
        if (UmengUtil.isActivityNotContainFragment(activity)) {
            MobclickAgent.onPageStart(activity.getClass().getSimpleName());
        }
        MobclickAgent.onResume(activity);
    }

    /**
     * 在 ActivityLifecycleCallbacks 的 onActivityPaused 回调方法里调用
     *
     * @param activity
     */
    public static void onActivityPaused(Activity activity) {
        if (UmengUtil.isActivityNotContainFragment(activity)) {
            // 保证 onPageEnd 在 onPause 之前调用,因为 onPause 中会保存信息
            MobclickAgent.onPageEnd(activity.getClass().getSimpleName());
        }
        MobclickAgent.onPause(activity);
    }
    // ======================== Activity END ========================

    // ======================== Fragment START ========================

    /**
     * 在 Fragment 的 setUserVisibleHint 方法里调用
     *
     * @param fragment
     * @param isVisibleToUser
     */
    public static void setFragmentUserVisibleHint(Fragment fragment, boolean isVisibleToUser) {
        if (fragment.isResumed()) {
            onVisibilityChangedToUser(fragment, isVisibleToUser);
        }
    }

    /**
     * 在 Fragment 的 onResume 方法里调用
     *
     * @param fragment
     */
    public static void onFragmentResume(Fragment fragment) {
        if (fragment.getUserVisibleHint()) {
            onVisibilityChangedToUser(fragment, true);
        }
    }

    /**
     * 在 Fragment 的 onPause 方法里调用
     *
     * @param fragment
     */
    public static void onFragmentPause(Fragment fragment) {
        if (fragment.getUserVisibleHint()) {
            onVisibilityChangedToUser(fragment, false);
        }
    }

    private static void onVisibilityChangedToUser(Fragment fragment, boolean isVisibleToUser) {
        if (isVisibleToUser) {
            MobclickAgent.onPageStart(fragment.getClass().getSimpleName());
        } else {
            MobclickAgent.onPageEnd(fragment.getClass().getSimpleName());
        }
    }
    // ======================== Fragment END ========================
}