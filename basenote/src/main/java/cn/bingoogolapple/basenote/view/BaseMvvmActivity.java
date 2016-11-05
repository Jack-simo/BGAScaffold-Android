package cn.bingoogolapple.basenote.view;

import android.databinding.ViewDataBinding;

import cn.bingoogolapple.basenote.presenter.BasePresenter;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午5:07
 * 描述:
 */
public abstract class BaseMvvmActivity<B extends ViewDataBinding, P extends BasePresenter> extends BaseMvpActivity<P> {
    protected B mBinding;
}