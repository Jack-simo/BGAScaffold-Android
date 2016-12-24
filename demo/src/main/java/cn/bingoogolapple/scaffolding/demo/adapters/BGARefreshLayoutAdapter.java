package cn.bingoogolapple.scaffolding.demo.adapters;

import android.databinding.BindingAdapter;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/20 下午6:28
 * 描述:
 */
public class BGARefreshLayoutAdapter {

    @BindingAdapter({"bga_refresh_delegate"})
    public static void setDelegate(BGARefreshLayout refreshLayout, BGARefreshLayout.BGARefreshLayoutDelegate delegate) {
        refreshLayout.setDelegate(delegate);
    }

}
