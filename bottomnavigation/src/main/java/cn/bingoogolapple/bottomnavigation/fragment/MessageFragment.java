package cn.bingoogolapple.bottomnavigation.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.baidu.mapapi.SDKInitializer;

import java.util.List;

import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.bottomnavigation.R;
import cn.bingoogolapple.bottomnavigation.activity.CountDownActivity;
import cn.bingoogolapple.bottomnavigation.activity.baidumap.BDMapActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/7/3 下午8:29
 * 描述:
 */
public class MessageFragment extends BaseMainFragment implements EasyPermissions.PermissionCallbacks {
    private static final int RC_PERMISSIONS_BDMAP = 1;
    private static final int RC_SETTINGS_SCREEN = 1;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_message);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mTitlebar.setLeftText("百度地图");
        setTitle("CountDown");

        SDKInitializer.initialize(mApp);
    }

    @AfterPermissionGranted(RC_PERMISSIONS_BDMAP)
    @Override
    public void onClickLeft() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            mActivity.forward(BDMapActivity.class);
        } else {
            EasyPermissions.requestPermissions(this, "访问百度地图需要授权访问位置的权限!", RC_PERMISSIONS_BDMAP, perms);
        }
    }

    @Override
    protected void onClickTitle() {
        mActivity.forward(CountDownActivity.class);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        ToastUtil.show("权限被同意");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtil.show("权限被拒绝");

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, "应用程序缺少位置访问权限,百度地图将无法正常工作,打开设置界面修改权限")
                    .setTitle("权限请求")
                    .setPositiveButton("设置")
                    .setNegativeButton("取消", null)
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SETTINGS_SCREEN) {
            ToastUtil.show("修改权限");
        }

    }
}