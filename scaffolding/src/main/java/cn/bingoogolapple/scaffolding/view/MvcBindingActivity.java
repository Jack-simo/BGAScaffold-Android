package cn.bingoogolapple.scaffolding.view;

import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.transition.TransitionManager;
import android.view.ViewGroup;

import cn.bingoogolapple.scaffolding.BR;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午5:07
 * 描述:
 */
public abstract class MvcBindingActivity<B extends ViewDataBinding> extends MvcActivity {
    protected B mBinding;

    @Override
    protected void initContentView() {
        mBinding = DataBindingUtil.setContentView(this, getRootLayoutResID());
        mBinding.setVariable(BR.eventHandler, this);
    }

    @CallSuper
    @Override
    protected void setListener() {
        mBinding.addOnRebindCallback(new OnRebindCallback() {
            @Override
            public boolean onPreBind(ViewDataBinding binding) {
                ViewGroup view = (ViewGroup) binding.getRoot();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(view);
                }
                return true;
            }
        });
    }
}