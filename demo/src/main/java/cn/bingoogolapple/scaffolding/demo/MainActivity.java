package cn.bingoogolapple.scaffolding.demo;

import android.Manifest;
import android.os.Bundle;

import java.util.List;

import cn.bingoogolapple.scaffolding.demo.databinding.ActivityMainBinding;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.activity.EmActivity;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午5:53
 * 描述:
 */
public class MainActivity extends MvcBindingActivity<ActivityMainBinding> {
    /**
     * 权限请求码
     */
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    /**
     * 跳转到权限设置界面的请求码
     */
    private static final int REQUEST_CODE_SETTINGS_SCREEN = 1;

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        requestPermissions();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, "缺少权限时「" + AppManager.getInstance().getAppName() + "」将无法正常工作,打开设置界面修改权限")
                    .setTitle("权限请求")
                    .setPositiveButton("设置")
                    .setNegativeButton("取消", (dialog, which) -> finish())
                    .setRequestCode(REQUEST_CODE_SETTINGS_SCREEN)
                    .build()
                    .show();
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS)
    public void requestPermissions() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "使用「" + AppManager.getInstance().getAppName() + "」需要授权权限!", REQUEST_CODE_PERMISSIONS, perms);
        }
    }

    @Override
    public void onBackPressed() {
        AppManager.getInstance().exitWithDoubleClick();
    }

    /**
     * 跳转到环信案例主界面
     */
    public void goToEm() {
        forward(EmActivity.class);
    }
}