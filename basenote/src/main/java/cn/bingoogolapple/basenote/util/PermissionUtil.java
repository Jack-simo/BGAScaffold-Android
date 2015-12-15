package cn.bingoogolapple.basenote.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.basenote.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/15 下午10:48
 * 描述:
 */
public class PermissionUtil {

    private PermissionUtil() {
    }

    public static void request(final Activity activity, final int requestCode, final Delegate delegate, String... permissionArr) {
        List<String> permissionsNeeded = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();

        for (String permission : permissionArr) {
            if (!addPermission(activity, permissionsList, permission)) {
                permissionsNeeded.add(permission.substring(permission.lastIndexOf(".") + 1));
            }
        }

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                StringBuilder messageSb = new StringBuilder(permissionsNeeded.get(0));
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    messageSb.append("\n").append(permissionsNeeded.get(i));
                }
                new AlertDialog.Builder(activity)
                        .setTitle("You need to grant access to")
                        .setMessage(messageSb.toString())
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), requestCode);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delegate.onPermissionDenied();
                            }
                        })
                        .create()
                        .show();
                return;
            }
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), requestCode);
            return;
        }
        delegate.onPermissionGranted();
    }

    private static boolean addPermission(Activity activity, List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return false;
            }
        }
        return true;
    }

    public static void result(String[] permissions, int[] grantResults, Delegate delegate, String... permissionArr) {
        Map<String, Integer> perms = new HashMap<>();
        for (String permission : permissionArr) {
            perms.put(permission, PackageManager.PERMISSION_GRANTED);
        }

        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
        }

        boolean granted = true;
        for (String permission : permissionArr) {
            if (perms.get(permission) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }
        }

        if (granted) {
            delegate.onPermissionGranted();
        } else {
            delegate.onPermissionDenied();
        }
    }

    public interface Delegate {
        void onPermissionGranted();

        void onPermissionDenied();
    }
}