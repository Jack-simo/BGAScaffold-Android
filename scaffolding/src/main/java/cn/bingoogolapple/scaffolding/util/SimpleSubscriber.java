package cn.bingoogolapple.scaffolding.util;

import android.app.Activity;
import android.support.annotation.StringRes;

import cn.bingoogolapple.scaffolding.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/14 上午1:15
 * 描述:
 */
public abstract class SimpleSubscriber<T> extends LocalSubscriber<T> {

    public SimpleSubscriber() {
    }

    public SimpleSubscriber(Activity activity) {
        super(activity);
    }

    public SimpleSubscriber(Activity activity, boolean cancelable) {
        super(activity, cancelable);
    }

    public SimpleSubscriber(Activity activity, @StringRes int resId) {
        super(activity, resId);
    }

    public SimpleSubscriber(Activity activity, @StringRes int resId, boolean cancelable) {
        super(activity, resId, cancelable);
    }

    public SimpleSubscriber(Activity activity, String msg) {
        super(activity, msg);
    }

    public SimpleSubscriber(Activity activity, String msg, boolean cancelable) {
        super(activity, msg, cancelable);
    }

    @Override
    public void onError(Throwable e) {
        if (AppManager.isBuildDebug()) {
            e.printStackTrace();
        }

        dismissLoadingDialog();

        if (!NetUtil.isNetworkAvailable()) {
            onError(AppManager.getApp().getString(R.string.network_unavailable));
        } else if (e instanceof ServerException) {
            onError(e.getMessage());
        } else {
            onError(AppManager.getApp().getString(R.string.try_again_later));
        }
    }
}
