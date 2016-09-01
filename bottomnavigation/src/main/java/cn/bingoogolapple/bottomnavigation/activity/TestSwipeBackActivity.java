package cn.bingoogolapple.bottomnavigation.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.List;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
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
        mWebAppInterface = new WebAppInterface();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(mWebAppInterface, "app");
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
            mWebView.loadUrl("http://www.bingoogolapple.cn");
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

    class WebAppInterface {
        @JavascriptInterface
        public void sendMsg(String msg) {
            ToastUtil.show(msg);
        }

        public void receiveMsg(String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:receiveMsg('" + msg + "')");
                }
            });
        }
    }
}
