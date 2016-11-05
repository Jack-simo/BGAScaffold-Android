package cn.bingoogolapple.basenote.view;

import cn.bingoogolapple.basenote.presenter.BasePresenter;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午5:07
 * 描述:
 */
public abstract class BaseMvpActivity<P extends BasePresenter> extends BaseActivity implements BaseView {
    protected P mPresenter;

    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }
}