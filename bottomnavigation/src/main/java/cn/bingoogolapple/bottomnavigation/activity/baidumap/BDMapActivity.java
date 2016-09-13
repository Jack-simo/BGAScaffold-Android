package cn.bingoogolapple.bottomnavigation.activity.baidumap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.bottomnavigation.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/9/13 上午11:28
 * 描述:
 */
public class BDMapActivity extends TitlebarActivity {
    private MapView mMapView;
    private BroadcastReceiver mSDKBroadcastReceiver;
    private BaiduMap mBaiduMap;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bdmap);
        mMapView = getViewById(R.id.bmapView);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        registerSDKCheckReceiver();

//        mMapView.showScaleControl(false);
//        mMapView.showZoomControls(false);

        mBaiduMap = mMapView.getMap();

        Logger.i(TAG, "ZoomLevel = " + mBaiduMap.getMinZoomLevel() + " MaxZoomLevel = " + mBaiduMap.getMaxZoomLevel() + " zoom = " + mBaiduMap.getMapStatus().zoom);
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(30.67994285, 104.06792346)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(30.67994285, 104.06792346), 16));

//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(30.67994285, 104.06792346), 16), 2000);


//        MapStatus mapStatus = new MapStatus.Builder().overlook(45).build();
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));

        UiSettings uiSettings = mBaiduMap.getUiSettings();
//        uiSettings.setCompassEnabled(false);

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mBaiduMap.setTrafficEnabled(true);
    }

    private void registerSDKCheckReceiver() {
        mSDKBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR) {
                    ToastUtil.show("网络错误");
                } else if (intent.getAction() == SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR) {
                    ToastUtil.show("API_KEY验证失败");
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        intentFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        registerReceiver(mSDKBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        if (mSDKBroadcastReceiver != null) {
            unregisterReceiver(mSDKBroadcastReceiver);
        }
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
}
