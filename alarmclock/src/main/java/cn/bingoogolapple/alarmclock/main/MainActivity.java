package cn.bingoogolapple.alarmclock.main;

import android.Manifest;
import android.os.Bundle;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.plans.PlansActivity;
import cn.bingoogolapple.alarmclock.plans.PlansFragmentActivity;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.view.MvcActivity;
import cn.bingoogolapple.scaffolding.view.TopBarType;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午5:53
 * 描述:
 */
public class MainActivity extends MvcActivity {
    /**
     * 权限请求码
     */
    private static final int REQUEST_CODE_PERMISSIONS = 1;

    @Override
    protected TopBarType getTopBarType() {
        return TopBarType.TitleBar;
    }

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void setListener() {
        setOnClick(R.id.activity_tv, object -> forward(PlansActivity.class));
        setOnClick(R.id.fragment_tv, object -> forward(PlansFragmentActivity.class));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle(R.string.app_name);
        mTitleBar.hiddenLeftCtv();

        requestPermissions();
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS)
    public void requestPermissions() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {

        } else {
            EasyPermissions.requestPermissions(this, "使用「" + AppManager.getInstance().getAppName() + "」需要授权读写外部存储权限!", REQUEST_CODE_PERMISSIONS, perms);
        }
    }

    @Override
    public void onBackPressed() {
        AppManager.getInstance().exitWithDoubleClick();
    }
}