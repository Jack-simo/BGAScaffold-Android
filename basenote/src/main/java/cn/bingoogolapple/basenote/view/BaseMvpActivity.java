package cn.bingoogolapple.basenote.view;

import android.support.annotation.StringRes;

import com.trello.rxlifecycle.LifecycleProvider;

import cn.bingoogolapple.basenote.presenter.BasePresenter;
import cn.bingoogolapple.basenote.util.ToastUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午5:07
 * 描述:
 */
public abstract class BaseMvpActivity<P extends BasePresenter> extends BaseActivity implements BaseView {
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
        return this;
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }
}