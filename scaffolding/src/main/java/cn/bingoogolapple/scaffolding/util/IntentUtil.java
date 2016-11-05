package cn.bingoogolapple.scaffolding.util;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/27 下午4:22
 * 描述:
 */
public class IntentUtil {

    private IntentUtil() {
    }

    /**
     * 获取安装apk文件的意图
     *
     * @param apkFile apk文件
     * @return
     */
    public Intent getInstallApkIntent(File apkFile) {
        Intent installApkIntent = new Intent();
        installApkIntent.setAction(Intent.ACTION_VIEW);
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installApkIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        return installApkIntent;
    }

    /**
     * 获取打电话的意图
     *
     * @param phone 电话号码
     * @return
     */
    public Intent getCallUpIntent(String phone) {
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
    }
}
