package cn.bingoogolapple.basenote.util;

import rx.Subscriber;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/27 下午11:48
 * 描述:
 */
public abstract class SimpleSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        ToastUtil.show(e.getMessage());
    }
}