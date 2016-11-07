package cn.bingoogolapple.scaffolding.util;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;

import cn.bingoogolapple.scaffolding.R;
import rx.Subscriber;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/14 上午1:15
 * 描述:
 */
public abstract class LocalSubscriber<T> extends Subscriber<T> {
    protected MaterialDialog mLoadingDialog;
    protected Activity mActivity;
    protected String mMsg;
    protected boolean mCancelable;

    public LocalSubscriber() {
    }

    public LocalSubscriber(Activity activity) {
        this(activity, R.string.loading);
    }

    public LocalSubscriber(Activity activity, boolean cancelable) {
        this(activity, R.string.loading, cancelable);
    }

    public LocalSubscriber(Activity activity, @StringRes int resId) {
        this(activity, activity.getString(resId));
    }

    public LocalSubscriber(Activity activity, @StringRes int resId, boolean cancelable) {
        this(activity, activity.getString(resId), cancelable);
    }

    public LocalSubscriber(Activity activity, String msg) {
        this(activity, msg, true);
    }

    public LocalSubscriber(Activity activity, String msg, boolean cancelable) {
        mActivity = activity;
        mMsg = msg;
        mCancelable = cancelable;
    }

    @Override
    public void onStart() {
        if (mActivity != null && StringUtil.isNotEmpty(mMsg)) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity)
                    .content(mMsg)
                    .progress(true, 0);

            if (mCancelable) {
                // 点击取消的时候取消订阅
                builder.cancelListener(dialog -> {
                    if (!isUnsubscribed()) {
                        unsubscribe();
                    }
                });
            }

            mLoadingDialog = builder.show();
        }
    }

    @CallSuper
    @Override
    public void onCompleted() {
        dismissLoadingDialog();
    }

    @Override
    public void onError(Throwable e) {
        if (AppManager.getInstance().isBuildDebug()) {
            e.printStackTrace();
        }

        dismissLoadingDialog();

        onError(AppManager.getApp().getString(R.string.try_again_later));
    }

    /**
     * 隐藏加载对话框
     */
    protected void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    public void onError(String msg) {
        ToastUtil.show(msg);
    }
}
