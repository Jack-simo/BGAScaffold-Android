package cn.bingoogolapple.scaffolding.demo;

import android.Manifest;
import android.os.Bundle;

import cn.bingoogolapple.scaffolding.demo.databinding.ActivityMainBinding;
import cn.bingoogolapple.scaffolding.demo.greendao.activity.GreenDaoActivity;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.PermissionUtil;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午5:53
 * 描述:
 */
public class MainActivity extends MvcBindingActivity<ActivityMainBinding> {

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        requestPermissions();
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

    /**
     * 跳转到 GreenDao 界面
     */
    public void goToGreenDao() {
        forward(GreenDaoActivity.class);
    }

}