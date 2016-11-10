package cn.bingoogolapple.scaffolding.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:34
 * 描述:
 */
public class BGABindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private T mBinding;

    public BGABindingViewHolder(T binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public T getBinding() {
        return mBinding;
    }
}