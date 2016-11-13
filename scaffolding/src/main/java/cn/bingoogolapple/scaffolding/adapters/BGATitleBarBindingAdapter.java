package cn.bingoogolapple.scaffolding.adapters;

import android.databinding.BindingAdapter;

import cn.bingoogolapple.titlebar.BGATitleBar;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/13 下午3:57
 * 描述:
 */
public class BGATitleBarBindingAdapter {

    @BindingAdapter("bgatitlebar_delegate")
    public static void setDelegate(BGATitleBar titleBar, final BGATitleBar.Delegate delegate) {
        titleBar.setDelegate(delegate);
    }

    @BindingAdapter("bgatitlebar_titleText")
    public static void setTitleText(BGATitleBar titleBar, String text) {
        titleBar.setTitleText(text);
    }

    @BindingAdapter("bgatitlebar_rightText")
    public static void setRightText(BGATitleBar titleBar, String text) {
        titleBar.setRightText(text);
    }

}
