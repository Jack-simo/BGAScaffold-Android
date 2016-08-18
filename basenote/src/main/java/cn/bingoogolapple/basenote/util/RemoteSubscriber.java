package cn.bingoogolapple.basenote.util;

import android.app.Activity;
import android.support.annotation.StringRes;

import cn.bingoogolapple.basenote.App;
import cn.bingoogolapple.basenote.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/14 上午1:15
 * 描述:
 */
public abstract class RemoteSubscriber<T> extends LocalSubscriber<T> {

    public RemoteSubscriber() {
    }

    public RemoteSubscriber(Activity activity) {
        super(activity);
    }

    public RemoteSubscriber(Activity activity, boolean cancelable) {
        super(activity, cancelable);
    }

    public RemoteSubscriber(Activity activity, @StringRes int resId) {
        super(activity, resId);
    }

    public RemoteSubscriber(Activity activity, @StringRes int resId, boolean cancelable) {
        super(activity, resId, cancelable);
    }

    public RemoteSubscriber(Activity activity, String msg) {
        super(activity, msg);
    }

    public RemoteSubscriber(Activity activity, String msg, boolean cancelable) {
        super(activity, msg, cancelable);
    }

    @Override
    public void onError(Throwable e) {
        dismissLoadingDialog();

        if (!NetUtil.isNetworkAvailable()) {
            onError(App.getInstance().getString(R.string.network_unavailable));
        } else if (e instanceof ApiException) {
            onError(e.getMessage());
        } else {
            onError(App.getInstance().getString(R.string.try_again_later));
        }
    }
}
