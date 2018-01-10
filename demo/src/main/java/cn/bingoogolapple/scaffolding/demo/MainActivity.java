package cn.bingoogolapple.scaffolding.demo;

import android.Manifest;
import android.os.Bundle;

import cn.bingoogolapple.scaffolding.demo.database.activity.DatabaseActivity;
import cn.bingoogolapple.scaffolding.demo.rxjava.activity.RxJavaActivity;
import cn.bingoogolapple.scaffolding.demo.rxjava.util.RxUtil;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.PermissionUtil;
import cn.bingoogolapple.scaffolding.view.MvcActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午5:53
 * 描述:
 */
public class MainActivity extends MvcActivity {

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected void setListener() {
        setOnClick(R.id.btn_main_rxjava, o -> forward(RxJavaActivity.class));
        setOnClick(R.id.btn_main_database, o -> forward(DatabaseActivity.class));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        RxUtil.runInUIThreadDelay(1, 1000, this).subscribe(dummy -> requestPermissions());
    }

    @AfterPermissionGranted(PermissionUtil.RC_PERMISSION_STORAGE)
    public void requestPermissions() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            PermissionUtil.requestPermissions(this, R.string.permission_request_storage, PermissionUtil.RC_PERMISSION_STORAGE, perms);
        }
    }

    @Override
    public void onBackPressed() {
        AppManager.getInstance().exitWithDoubleClick();
    }
}