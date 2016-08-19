package cn.bingoogolapple.gank.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import cn.bingoogolapple.basenote.view.ToolbarActivity;
import cn.bingoogolapple.gank.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/19 下午2:57
 * 描述:
 */
public class WebActivity extends ToolbarActivity<WebPresenter> implements WebPresenter.View {
    public static final String EXTRA_URL = "EXTRA_URL";
    public static final String EXTRA_DESC = "EXTRA_DESC";

    private NumberProgressBar mNumberProgressBar;
    private WebView mWebView;

    public static Intent newIntent(Context context, String url, String desc) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_DESC, desc);
        return intent;
    }

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_web);
        mNumberProgressBar = getViewById(R.id.numberProgressBar);
        mWebView = getViewById(R.id.webView);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle(getIntent().getStringExtra(EXTRA_DESC));
        mPresenter = new WebPresenterImpl(this);
        mPresenter.setWebViewSettings(mWebView, getIntent().getStringExtra(EXTRA_URL));
    }

    @Override
    public void updateProgressBar(int progress) {
        mNumberProgressBar.setProgress(progress);
        if (progress == 100) {
            mNumberProgressBar.setVisibility(View.GONE);
        } else {
            mNumberProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setWebTitle(String title) {
        setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mPresenter.refresh(mWebView);
                break;
            case R.id.action_copy_url:
                mPresenter.copyUrl(mWebView.getUrl());
                break;
            case R.id.action_open_in_browser:
                mPresenter.openInBrowser(mWebView.getUrl());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (mWebView != null) {
            mWebView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}