package cn.bingoogolapple.bottomnavigation.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import java.util.List;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.basenote.util.NetUtil;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.bottomnavigation.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/12 下午11:25
 * 描述:
 */
public class WebViewActivity extends TitlebarActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private WebView mWebView;
    private WebAppInterface mWebAppInterface;
    private FrameLayout mVideoFull;
    private View mCustomView;
    private MyWebChromeClient mWebChromeClient;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_webview);
        mWebView = getViewById(R.id.webView);
        mVideoFull = getViewById(R.id.video_full);
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.load_from_assets);
        setOnClickListener(R.id.load_from_sdcard);
        setOnClickListener(R.id.load_from_remote);
        setOnClickListener(R.id.java_call_js);
        setOnClickListener(R.id.get_string_from_js);
        setOnClickListener(R.id.load_video);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("WebView学习笔记");

        initWebSettings();
    }

    private void initWebSettings() {
        mWebAppInterface = new WebAppInterface();
        mWebView.addJavascriptInterface(mWebAppInterface, "app");
        mWebView.requestFocusFromTouch();

        WebSettings settings = mWebView.getSettings();
        settings.setPluginState(WebSettings.PluginState.ON);
        // WebView默认是不支持JavaScript的,这里设置支持JavaScript
        settings.setJavaScriptEnabled(true);
        if (NetUtil.isNetworkAvailable()) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            // 设置优先从缓存加载
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        String cacheDirPath = getCacheDir().getAbsolutePath() + "/webViewCache ";
        //开启 database storage API 功能
        settings.setDatabaseEnabled(true);
        //设置数据库缓存路径
        settings.setDatabasePath(cacheDirPath);

        //开启Application H5 Caches 功能
        settings.setAppCacheEnabled(true);
        //设置Application Caches 缓存目录
        // In order for the Application Caches API to be enabled,
        // this method must be called with a path to which the application can write.
        // This method should only be called once: repeated calls are ignored.
        settings.setAppCachePath(cacheDirPath);

        settings.setSaveFormData(false);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        /**
         * 为4.4以上系统在onPageFinished时再恢复图片加载时,如果存在多张图片引用的是相同的src时，
         * 会只有一个image标签得到加载，因而对于这样的系统我们就先直接加载。
         */
        if (Build.VERSION.SDK_INT >= 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }

        /**
         * 开启硬件加速后，WebView渲染页面更加快速，拖动也更加顺滑。但有个副作用就是容易会出现页面加载白块同时界面闪烁现象。
         * 解决这个问题的方法是设置WebView暂时关闭硬件加速
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        /**
         * 如果没有提供 WebViewClient 对象，则 WebView 会请求 Activity 管理者选择合适的 URL 处理方式，一般情况就是启动浏览器来加载URL；
         * 如果提供了 WebViewClient 对象且shouldOverrideUrlLoading 方法返回 true，则由当前应用自己处理URL,需调用view.loadUrl(url),否则不会加载；
         * 如果提供了 WebViewClient 对象且shouldOverrideUrlLoading 方法返回 false，则当前 WebView 处理URL；
         */
        mWebView.setWebViewClient(new WebViewClient() {
            // 默认就是返回false。如果不拦截特定的url,不用重写该方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (TextUtils.isEmpty(url)) {
                    return true;
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }

                try {
                    // 这里try一下,避免ActivityNotFoundException导致应用闪退
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            // API 21及以上系统版本才支持该方法
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                request.getUrl();
//                request.getRequestHeaders();
//                request.getMethod();
//                request.isForMainFrame();
//                request.isRedirect();
//                return super.shouldInterceptRequest(view, request);
//            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                showLoadingDialog(R.string.loading_data_tip);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                dismissLoadingDialog();
                if (!view.getSettings().getLoadsImagesAutomatically()) {
                    view.getSettings().setLoadsImagesAutomatically(true);
                }

                /**
                 * Uncaught ReferenceError: functionName is not defined
                 * 问题出现原因，网页的js代码没有加载完成，就调用了js方法。解决方法是在网页加载完成之后调用js方法
                 * 即在 onPageFinished 方法里调用js里的方法
                 */
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                dismissLoadingDialog();
                // 根据错误码展示具体的错误界面。展示原生的错误界面或加载本地的html错误界面
                Logger.i(TAG, "errorCode =" + errorCode + " description = " + description + " failingUrl = " + failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 当加载https时候，需要加入下面代码,接受所有证书
                handler.proceed();
            }
        });

        mWebChromeClient = new MyWebChromeClient();
        // 如果不 setWebChromeClient,那么 html 里的 Alert 将无法弹出
        mWebView.setWebChromeClient(mWebChromeClient);

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
        } else if (v.getId() == R.id.load_from_remote) {
            mWebView.loadUrl("http://shouji.baidu.com/software/9782214.html");
        } else if (v.getId() == R.id.java_call_js) {
            mWebAppInterface.javaCallJs("来自java的消息");
        } else if (v.getId() == R.id.get_string_from_js) {
            mWebAppInterface.getStringFromJs();
        } else if (v.getId() == R.id.load_video) {
            mWebView.loadUrl("http://gank.io");
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
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
            mWebView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.pauseTimers();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mCustomView != null) {
            mWebChromeClient.onHideCustomView();
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        // 默认的视频展示图
        private Bitmap mDefaultVideoPoster;

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Logger.i(TAG, "progress:" + newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            setTitle(title);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            // 处理html中的_bank标签多窗口问题
            WebView.HitTestResult result = view.getHitTestResult();
            String data = result.getExtra();
            mWebView.loadUrl(data);
            return true;
        }

        // 播放网络视频时全屏会被调用的方法
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            mVideoFull.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mVideoFull.setVisibility(View.VISIBLE);
        }

        // 视频播放退出全屏会被调用的
        @Override
        public void onHideCustomView() {
            // 不是全屏播放状态
            if (mCustomView == null) {
                return;
            }

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mCustomView.setVisibility(View.GONE);
            mVideoFull.removeView(mCustomView);
            mCustomView = null;
            mVideoFull.setVisibility(View.GONE);
            mCustomViewCallback.onCustomViewHidden();
            mWebView.setVisibility(View.VISIBLE);
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            if (mDefaultVideoPoster == null) {
                mDefaultVideoPoster = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                return mDefaultVideoPoster;
            }
            return super.getDefaultVideoPoster();
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            Logger.i(TAG, "onJsPrompt: url = " + url + " message = " + message + " defaultValue = " + defaultValue);
//            return super.onJsPrompt(view, url, message, defaultValue, result);
            // 返回true表示自己处理,如果这里不手动掉一次confirm/cancel方法的话界面上的按钮不会失去焦点,整个应用的webview都会出问题,接收不到触摸事件
            result.confirm();
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Logger.i(TAG, "onJsAlert: url = " + url + " message = " + message);
//            return super.onJsAlert(view, url, message, result);
            // 返回true表示自己处理,如果这里不手动掉一次confirm/cancel方法的话界面上的按钮不会失去焦点,整个应用的webview都会出问题,接收不到触摸事件
            result.confirm();
            return true;
        }

        // Confirm方式基本上是耗时最短和最稳定的
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            Logger.i(TAG, "onJsConfirm: url = " + url + " message = " + message);
//            return super.onJsConfirm(view, url, message, result);
            // 返回true表示自己处理,如果这里不手动掉一次confirm/cancel方法的话界面上的按钮不会失去焦点,整个应用的webview都会出问题,接收不到触摸事件
            result.confirm();
            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Logger.i(TAG, "onConsoleMessage: message = " + consoleMessage.message() + " sourceId = " + consoleMessage.sourceId());
//            return super.onConsoleMessage(consoleMessage);
            // 返回true表示自己处理,webview将不会再打印该日志
            return true;
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
        public void jsCallJava(String msg) {
            ToastUtil.show(msg);
        }

        public void javaCallJs(String msg) {
            /**
             * All WebView methods must be called on the same thread
             * 在js调用后的Java回调线程并不是主线程,需将webview操作放在主线程
             */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:javaCallJs('" + msg + "')");
                }
            });
        }

        @JavascriptInterface
        public String getStringFromJava() {
            return "来自Java的String";
        }

        public void getStringFromJs() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mWebView.evaluateJavascript("getStringFromJs", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                ToastUtil.show(value);
                            }
                        });
                    }
                }
            });
        }
    }
}
/**
 * 从网络上下载html页面的过程应放在工作线程(后台线程)中
 * html下载成功后渲染出html的步骤应放在UI主线程,不然WebView加载网页过程会容易报错
 * <p>
 * 使用本地浏览器打开网页
 * Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://image.baidu.com"));
 * startActivity(intent);
 */

// WebView·开车指南    http://mp.weixin.qq.com/s?__biz=MzI3NDM3Mjg5NQ==&mid=2247483682&idx=1&sn=b1e03bfb789f75467c351a8ed7dfc156&scene=0#rd

// 史上最全WebView使用，附送Html5Activity一份   http://www.jianshu.com/p/3fcf8ba18d7f

// Android WebView的Js对象注入漏洞解决方案   http://blog.csdn.net/leehong2005/article/details/11808557