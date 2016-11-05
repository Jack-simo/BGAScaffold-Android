package cn.bingoogolapple.scaffolding.view;

import android.support.annotation.StringRes;

import com.trello.rxlifecycle.LifecycleProvider;

import cn.bingoogolapple.scaffolding.presenter.BasePresenter;
import cn.bingoogolapple.scaffolding.util.ToastUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午10:57
 * 描述:
 */
public abstract class BaseMvpFragment<P extends BasePresenter> extends BaseFragment implements BaseView {
    protected P mPresenter;

    @Override
    public void showMsg(@StringRes int resId) {
        ToastUtil.show(resId);
    }

    @Override
    public void showMsg(String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }
}