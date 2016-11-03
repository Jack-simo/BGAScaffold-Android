package cn.bingoogolapple.basenote.presenter;

import cn.bingoogolapple.basenote.view.BaseView;
import rx.subscriptions.CompositeSubscription;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/18 下午8:41
 * 描述:
 */
public class BasePresenterImpl<T extends BaseView> implements BasePresenter {
    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    protected T mView;

    public BasePresenterImpl(T view) {
        mView = view;
    }

    @Override
    public void onDestroy() {
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
    }
}
