package cn.bingoogolapple.amap.util;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/11 下午4:27
 * 描述:
 */
public class SimpleLocationManager {
    private Context mContext;
    private AMapLocationClient mAMapLocationClient;

    public SimpleLocationManager(Context context) {
        mContext = context;
    }

    /**
     * 请求获取位置信息
     *
     * @param onceLocation         是否只定位一次
     * @param aMapLocationListener 定位回调接口，该回调接口中的方法不会立即被调用
     */
    public void requestLocation(boolean onceLocation, AMapLocationListener aMapLocationListener) {
        stopLocation();
        if (mAMapLocationClient == null) {
            AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
            aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            aMapLocationClientOption.setOnceLocation(onceLocation);
            aMapLocationClientOption.setInterval(4000);

            mAMapLocationClient = new AMapLocationClient(mContext);
            mAMapLocationClient.setLocationOption(aMapLocationClientOption);
        }
        mAMapLocationClient.setLocationListener(aMapLocationListener);
        mAMapLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (mAMapLocationClient != null) {
            mAMapLocationClient.stopLocation();
        }
    }

    /**
     * 销毁定位客户端
     */
    public void onDestroy() {
        if (mAMapLocationClient != null) {
            mAMapLocationClient.onDestroy();
        }
        mAMapLocationClient = null;
    }
}