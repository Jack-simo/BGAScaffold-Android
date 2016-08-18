package cn.bingoogolapple.basenote.view;

import android.support.annotation.StringRes;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/18 下午8:49
 * 描述:
 */
public interface BaseView {

    void showMsg(@StringRes int resId);

    void showMsg(String msg);
}