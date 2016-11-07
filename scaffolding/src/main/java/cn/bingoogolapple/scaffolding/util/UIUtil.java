package cn.bingoogolapple.scaffolding.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/13 下午11:16
 * 描述:
 */
public class UIUtil {
    private UIUtil() {
    }

    public static int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, AppManager.getApp().getResources().getDisplayMetrics());
    }

    public static int sp2px(float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, AppManager.getApp().getResources().getDisplayMetrics());
    }

    public static int getColor(@ColorRes int id) {
        return AppManager.getApp().getResources().getColor(id);
    }

    public static int getStatusBarHeight() {
        Resources resources = AppManager.getApp().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static int getStatusBarHeight(View view) {
        Rect rectangle = new Rect();
        view.getRootView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    public static int getNavigationBarHeight() {
        int navigationBarHeight = 0;
        Resources resources = AppManager.getApp().getResources();
        int resourceId = resources.getIdentifier(resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
        if (resourceId > 0 && checkDeviceHasNavigationBar()) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return navigationBarHeight;
    }

    public static boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = AppManager.getApp().getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) AppManager.getApp().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        WindowManager windowManager = (WindowManager) AppManager.getApp().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}