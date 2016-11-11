package cn.bingoogolapple.scaffolding.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:36
 * 描述:在子类的 getItemViewType 方法中，把 item 的布局文件资源 id 作为返回值
 */
public abstract class BGABindingRecyclerViewAdapter extends RecyclerView.Adapter<BGABindingViewHolder> {
    private LayoutInflater mLayoutInflater;

    protected LayoutInflater getLayoutInflater(View view) {
        if (mLayoutInflater == null) {
            mLayoutInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return mLayoutInflater;
    }

    @Override
    public BGABindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BGABindingViewHolder(DataBindingUtil.inflate(getLayoutInflater(parent), viewType, parent, false));
    }
}
