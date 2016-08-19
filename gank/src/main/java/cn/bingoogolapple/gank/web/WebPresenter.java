package cn.bingoogolapple.gank.web;

import android.webkit.WebView;

import cn.bingoogolapple.basenote.presenter.BasePresenter;
import cn.bingoogolapple.basenote.view.BaseView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/19 下午1:47
 * 描述:
 */
public interface WebPresenter extends BasePresenter {

    interface View extends BaseView {
        void updateProgressBar(int progress);

        void setWebTitle(String title);
    }

    void setWebViewSettings(WebView webView, String url);

    void openInBrowser(String url);

    void copyUrl(String url);

    void refresh(WebView webView);
}