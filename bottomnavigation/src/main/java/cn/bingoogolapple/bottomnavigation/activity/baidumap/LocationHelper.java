package cn.bingoogolapple.bottomnavigation.activity.baidumap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

import cn.bingoogolapple.basenote.App;
import cn.bingoogolapple.basenote.util.StringUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/10/30 上午1:12
 * 描述:百度地图定位工具类
 */
public class LocationHelper implements BDLocationListener {
    private LocationClient mLocationClient;
    private WeakReference<Delegate> mDelegate;

    public LocationHelper(Delegate delegate) {
        mLocationClient = new LocationClient(App.getInstance());
        mLocationClient.registerLocationListener(this);
        mDelegate = new WeakReference<>(delegate);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null) {
            return;
        }
        if (!(location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation)) {
            return;
        }
        if (mDelegate != null && mDelegate.get() != null) {
            mDelegate.get().onReceiveLocation(location, new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    /**
     * 请求获取位置信息
     *
     * @param onceLocation 是否只定位一次
     */
    public void requestLocation(boolean onceLocation) {
        stopLocation();
        mLocationClient.setLocOption(LocationHelper.getLocationClientOption(onceLocation));
        mLocationClient.start();
    }

    /**
     * 停止定位. 在Activity/Fragment的onPause方法中调用该方法
     */
    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
    }

    /**
     * 获取详细位置信息
     *
     * @param latLng
     */
    public void requestDetailAddress(LatLng latLng) {
        GeoCoder coder = GeoCoder.newInstance();
        ReverseGeoCodeOption reverseCode = new ReverseGeoCodeOption();
        ReverseGeoCodeOption result = reverseCode.location(latLng);
        boolean isSuccess = coder.reverseGeoCode(result);
        Logger.i("requestAddress isSuccess:" + isSuccess);

        coder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                String detailAddress = LocationHelper.getDetailAddress(result);
                if (StringUtil.isNotEmpty(detailAddress) && mDelegate != null && mDelegate.get() != null) {
                    mDelegate.get().onReceiveDetailAddress(detailAddress);
                }
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
            }
        });
    }

    /**
     * 销毁定位客户端. 在Activity/Fragment的onDestroy方法中调用该方法
     */
    public void onDestroy() {
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(this);
            mLocationClient.stop();
            mLocationClient = null;
            mDelegate.clear();
            mDelegate = null;
        }
    }

    public static LocationClientOption getLocationClientOption(boolean onceLocation) {
        LocationClientOption locationClientOption = new LocationClientOption();
        // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，默认gcj02，设置返回的定位结果坐标系
        locationClientOption.setCoorType("bd09ll");
        // 可选，默认false,设置是否使用gps
        locationClientOption.setOpenGps(true);
        // 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        if (onceLocation) {
            locationClientOption.setScanSpan(0);
        } else {
            locationClientOption.setScanSpan(2000);
        }
        //可选，默认false，设置是否需要地址信息
        locationClientOption.setIsNeedAddress(true);
        return locationClientOption;
    }

    /**
     * 获取详细地址
     *
     * @param result
     * @return
     */
    public static String getDetailAddress(ReverseGeoCodeResult result) {
        String address = "";
        if (result != null) {
            if (result.getAddress() != null) {
                address = result.getAddress();
            }
            if (result.getAddressDetail() != null) {
                ReverseGeoCodeResult.AddressComponent ac = result.getAddressDetail();
                if (StringUtil.isNotEmpty(ac.street)) {
                    address = ac.district + ac.street;
                    if (StringUtil.isNotEmpty(ac.streetNumber)) {
                        address = ac.district + ac.street + ac.streetNumber;
                    }
                }
            }
        }
        return address;
    }

    public interface Delegate {
        void onReceiveLocation(BDLocation location, LatLng latLng);

        void onReceiveDetailAddress(String detailAddress);
    }
}