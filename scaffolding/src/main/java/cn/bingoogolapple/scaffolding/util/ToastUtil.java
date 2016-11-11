package cn.bingoogolapple.scaffolding.util;

import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午5:17
 * 描述:
 */
public class ToastUtil {

    private ToastUtil() {
    }

    public static void show(CharSequence text) {
        if (StringUtil.isNotEmpty(text)) {
            if (text.length() < 10) {
                Toast.makeText(AppManager.getApp(), text, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AppManager.getApp(), text, Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void show(@StringRes int resId) {
        show(AppManager.getApp().getResources().getString(resId));
    }

    public static void showSafe(final CharSequence text) {
        RxUtil.runInUIThread(text).subscribe(msg -> show(msg));
    }

    public static void showSafe(@StringRes int resId) {
        showSafe(AppManager.getApp().getResources().getString(resId));
    }
}