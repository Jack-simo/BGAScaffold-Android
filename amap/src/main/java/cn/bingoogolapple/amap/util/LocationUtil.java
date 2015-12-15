package cn.bingoogolapple.amap.util;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import cn.bingoogolapple.basenote.App;

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
}