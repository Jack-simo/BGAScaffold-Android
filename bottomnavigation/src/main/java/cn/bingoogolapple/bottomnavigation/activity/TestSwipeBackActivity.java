package cn.bingoogolapple.bottomnavigation.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.bottomnavigation.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/12 下午11:25
 * 描述:
 */
public class TestSwipeBackActivity extends TitlebarActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private WebView mWebView;
    private WebAppInterface mWebAppInterface;

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_test_swipe_back);
        mWebView = getViewById(R.id.webView);
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.load_from_assets);
        setOnClickListener(R.id.load_from_sdcard);
        setOnClickListener(R.id.receive_msg);
        setOnClickListener(R.id.load_from_remote);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setLeftDrawable(R.drawable.selector_nav_back);
        setTitle("测试滑动返回");

        initWebSettings();
    }

    private void initWebSettings() {
        mWebAppInterface = new WebAppInterface();
        mWebView.addJavascriptInterface(mWebAppInterface, "app");

        WebSettings settings = mWebView.getSettings();
        // WebView默认是不支持JavaScript的,这里设置支持JavaScript
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        // 设置优先从缓存加载
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);

        /**
         * 如果没有提供 WebViewClient 对象，则 WebView 会请求 Activity 管理者选择合适的 URL 处理方式，一般情况就是启动浏览器来加载URL；
         * 如果提供了 WebViewClient 对象且shouldOverrideUrlLoading 方法返回 true，则由当前应用自己处理URL,需调用view.loadUrl(url),否则不会加载；
         * 如果提供了 WebViewClient 对象且shouldOverrideUrlLoading 方法返回 false，则当前 WebView 处理URL；
         */
        mWebView.setWebViewClient(new WebViewClient() {
            // 默认就是返回false。如果不拦截特定的url,不用重写该方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (!TextUtils.isEmpty(url)) {
//                    view.loadUrl(url);
//                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showLoadingDialog(R.string.loading_data_tip);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dismissLoadingDialog();
                /**
                 * Uncaught ReferenceError: functionName is not defined
                 * 问题出现原因，网页的js代码没有加载完成，就调用了js方法。解决方法是在网页加载完成之后调用js方法
                 * 即在 onPageFinished 方法里调用js里的方法
                 */
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                dismissLoadingDialog();
                // 根据错误码展示具体的错误界面。展示原生的错误界面或加载本地的html错误界面
                Logger.i(TAG, "errorCode =" + errorCode + " description = " + description + " failingUrl = " + failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 当加载https时候，需要加入下面代码,接受所有证书
                handler.proceed();
            }
        });

        // 如果不 setWebChromeClient,那么 html 里的 Alert 将无法弹出
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Logger.i(TAG, "progress:" + newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                // 这里处理文件下载
                Logger.i(TAG, "url = " + url + " userAgent = " + userAgent + " contentDisposition = " + contentDisposition + " mimetype = " + " contentLength = " + contentLength);

                if (url.endsWith(".apk")) {
                    // 调用系统浏览器下载文件,也可以自己根据url来写下载的逻辑
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.load_from_assets) {
            mWebView.loadUrl("file:///android_asset/index.html");
        } else if (v.getId() == R.id.load_from_sdcard) {
            loadSDCardHtml();
        } else if (v.getId() == R.id.receive_msg) {
            mWebAppInterface.receiveMsg("来自java的消息");
        } else if (v.getId() == R.id.load_from_remote) {
            mWebView.loadUrl("http://shouji.baidu.com/software/9782214.html");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS)
    private void loadSDCardHtml() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            mWebView.loadUrl("file:///sdcard/test/index.html");
        } else {
            EasyPermissions.requestPermissions(this, "需要访问SD卡的权限!", REQUEST_CODE_PERMISSIONS, perms);
        }
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

    class WebAppInterface {
        /**
         * 如果在没有混淆的版本运行正常，在混淆后的版本的代码运行错误，并提示Uncaught TypeError: Object [object Object] has no method，那就是没有做混淆例外处理。
         * <p>
         * keepattributes *Annotation*
         * keepattributes JavascriptInterface
         * -keep class cn.bingoogolapple.bottomnavigation.activity$WebAppInterface { *; }
         */

        @JavascriptInterface
        public void sendMsg(String msg) {
            ToastUtil.show(msg);
        }

        public void receiveMsg(String msg) {
            /**
             * All WebView methods must be called on the same thread
             * 在js调用后的Java回调线程并不是主线程,需将webview操作放在主线程
             */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:receiveMsg('" + msg + "')");
                }
            });
        }
    }
}

/**
 * 使用本地浏览器打开网页
 * Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://image.baidu.com"));
 * startActivity(intent);
 */