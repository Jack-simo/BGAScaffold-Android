package cn.bingoogolapple.amap.util;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.basenote.App;
import cn.bingoogolapple.basenote.util.ToastUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/11 下午9:08
 * 描述:
 */
public class LocationUtil {
    private static AMapLocationClientOption sAMapLocationClientOption;
    private static AMapLocationClient sAMapLocationClient;

    private LocationUtil() {
    }

    /**
     * 请求获取位置信息
     *
     * @param onceLocation         是否只定位一次
     * @param aMapLocationListener 定位回调接口，该回调接口中的方法不会立即被调用
     */
    public static void requestLocation(boolean onceLocation, AMapLocationListener aMapLocationListener) {
        stopLocation();
        if (sAMapLocationClient == null) {
            sAMapLocationClientOption = new AMapLocationClientOption();
            sAMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            sAMapLocationClientOption.setInterval(4000);
            sAMapLocationClient = new AMapLocationClient(App.getInstance());
        }
        sAMapLocationClientOption.setOnceLocation(onceLocation);
        sAMapLocationClient.setLocationOption(sAMapLocationClientOption);
        sAMapLocationClient.setLocationListener(aMapLocationListener);
        sAMapLocationClient.startLocation();
    }

    /**
     * 停止定位. 在Activity/Fragment的onPause方法中调用该方法
     */
    public static void stopLocation() {
        if (sAMapLocationClient != null) {
            sAMapLocationClient.stopLocation();
        }
    }

    /**
     * 销毁定位客户端. 在Activity/Fragment的onDestroy方法中调用该方法
     */
    public static void onDestroy() {
        stopLocation();
        if (sAMapLocationClient != null) {
            sAMapLocationClient.onDestroy();
        }
        sAMapLocationClientOption = null;
        sAMapLocationClient = null;
    }


    public static void requestLocationDataWrapper(final Activity activity, final int requestCode, LocationPermissionDelegate locationPermissionDelegate) {
        List<String> permissionsNeeded = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();

        if (!addPermission(activity, permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsNeeded.add("ACCESS_FINE_LOCATION");
        }
        if (!addPermission(activity, permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissionsNeeded.add("ACCESS_COARSE_LOCATION");
        }
        if (!addPermission(activity, permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add("WRITE_EXTERNAL_STORAGE");
        }

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    message = message + ", " + permissionsNeeded.get(i);
                }
                showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), requestCode);
                    }
                });
                return;
            }
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), requestCode);
            return;
        }
        locationPermissionDelegate.onPermissionGranted();
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

    public static void onRequestPermissionsResult(String[] permissions, int[] grantResults, LocationPermissionDelegate locationPermissionDelegate) {
        Map<String, Integer> perms = new HashMap<>();
        perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
        }

        if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionDelegate.onPermissionGranted();
        } else {
            ToastUtil.show("Some Permission is Denied");
        }
    }

    private static void showMessageOKCancel(Activity activity, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public interface LocationPermissionDelegate {
        void onPermissionGranted();
    }
}