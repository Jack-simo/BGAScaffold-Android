package cn.bingoogolapple.gank.web;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.bingoogolapple.basenote.presenter.BasePresenterImpl;
import cn.bingoogolapple.basenote.util.KeyboardUtil;
import cn.bingoogolapple.gank.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/19 下午3:20
 * 描述:
 */
public class WebPresenterImpl extends BasePresenterImpl<WebPresenter.View> implements WebPresenter {

    public WebPresenterImpl(View view) {
        super(view);
    }

    @Override
    public void setWebViewSettings(WebView webView, String url) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCacheEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);
        webView.setWebChromeClient(new ChromeClient());
        webView.setWebViewClient(new GankClient());
        webView.loadUrl(url);
    }

    @Override
    public void openInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(mView.getBaseActivity().getPackageManager()) != null) {
            mView.getBaseActivity().startActivity(intent);
        } else {
            mView.showMsg(R.string.open_url_failed);
        }
    }

    @Override
    public void copyUrl(String url) {
        KeyboardUtil.clip(mView.getBaseActivity(), url);
        mView.showMsg(R.string.copy_success);
    }

    @Override
    public void refresh(WebView webView) {
        webView.reload();
    }

    private class ChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mView.updateProgressBar(newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mView.setWebTitle(title);
        }
    }

    private class GankClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null) {
                view.loadUrl(url);
            }
            return true;
        }
    }
}