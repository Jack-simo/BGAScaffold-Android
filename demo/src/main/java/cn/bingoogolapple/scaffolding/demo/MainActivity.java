package cn.bingoogolapple.scaffolding.demo;

import android.Manifest;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bingoogolapple.scaffolding.demo.databinding.ActivityMainBinding;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.ConversationActivity;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.ToastUtil;
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
        mBinding.setEventHandler(this);

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
     * 显示选择环信账号对话框
     */
    public void showChooseEmAccountDialog() {
        new MaterialDialog.Builder(MainActivity.this)
                .title("请选择环信账号")
                .items("test1", "test2", "test3", "test4", "test5")
                .itemsCallback((dialog, itemView, position, text) -> {
                    emLogin(text.toString());
                })
                .show();
    }

    /**
     * 退出环信聊天服务器
     */
    public void emLogout() {
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                ToastUtil.showSafe("退出聊天服务器成功");
            }

            @Override
            public void onProgress(int progress, String status) {
                Logger.i("退出聊天服务器进度 progress:" + progress + " status:" + status);
            }

            @Override
            public void onError(int code, String message) {
                ToastUtil.showSafe("退出聊天服务器失败 code:" + code + " message:" + message);
            }
        });
    }

    /**
     * 登陆环信聊天服务器
     *
     * @param chatUsername 环信用户名
     */
    private void emLogin(String chatUsername) {
        EMClient.getInstance().login(chatUsername, "111111", new EMCallBack() {
            @Override
            public void onSuccess() {
                Logger.i("登录聊天服务器成功 chatUsername:" + chatUsername);

                EMClient.getInstance().chatManager().loadAllConversations();

                forward(ConversationActivity.class);
            }

            @Override
            public void onProgress(int progress, String status) {
                Logger.i("登录聊天服务器进度 progress:" + progress + " status:" + status);
            }

            @Override
            public void onError(int code, String message) {
                ToastUtil.showSafe("登录聊天服务器失败 code:" + code + " message:" + message);
            }
        });
    }
}