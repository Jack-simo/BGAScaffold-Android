package cn.bingoogolapple.scaffolding.view;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;

import cn.bingoogolapple.scaffolding.presenter.BasePresenter;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午5:07
 * 描述:
 */
public abstract class MvpBindingActivity<B extends ViewDataBinding, P extends BasePresenter> extends MvpActivity<P> {
    protected B mBinding;

    @Override
    protected void initContentView() {
        mBinding = DataBindingUtil.setContentView(this, getRootLayoutResID());
    }
}