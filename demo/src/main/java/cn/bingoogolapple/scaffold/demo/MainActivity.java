package cn.bingoogolapple.scaffold.demo;

import android.Manifest;
import android.os.Bundle;

import cn.bingoogolapple.scaffold.util.AppManager;
import cn.bingoogolapple.scaffold.util.PermissionUtil;
import cn.bingoogolapple.scaffold.view.MvcActivity;
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
        return cn.bingoogolapple.scaffold.demo.R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected void setListener() {
        setOnClick(R.id.btn_constraint_layout, view -> forward(ConstraintLayoutActivity.class));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @AfterPermissionGranted(PermissionUtil.RC_PERMISSION_STORAGE)
    public void requestPermissions() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            PermissionUtil.requestPermissions(this, cn.bingoogolapple.scaffold.demo.R.string.permission_request_storage, PermissionUtil.RC_PERMISSION_STORAGE, perms);
        }
    }

    @Override
    public void onBackPressed() {
        AppManager.getInstance().exitWithDoubleClick();
    }
}