package cn.bingoogolapple.amap.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.orhanobut.logger.Logger;

import cn.bingoogolapple.amap.R;
import cn.bingoogolapple.amap.util.LocationUtil;
import cn.bingoogolapple.amap.util.SimpleOnCameraChangeListener;
import cn.bingoogolapple.amap.util.SimpleOnGeocodeSearchListener;
import cn.bingoogolapple.amap.util.SimpleOnMarkerDragListener;
import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.PermissionUtil;
import cn.bingoogolapple.basenote.util.ToastUtil;

public class ChooseLocationDemo1Activity extends TitlebarActivity implements AMapLocationListener {
    private static final int REQUEST_CODE_LOCATION = 1;
    private AMap mAMap;
    private MapView mMapMv;
    private Marker mMarker;
    private GeocodeSearch mGeocoderSearch;

    private LatLng mLatLng;

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choose_location_demo1);
        mMapMv = getViewById(R.id.mv_choose_location_demo1_map);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("选择地理位置");

        mMapMv.onCreate(savedInstanceState);
        setUpMap();
    }

    private void setUpMap() {
        if (mAMap != null) {
            return;
        }

        mAMap = mMapMv.getMap();
        mAMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mLatLng = latLng;
                refreshMap();
            }
        });
        mAMap.setOnMarkerDragListener(new SimpleOnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.hideInfoWindow();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mLatLng = marker.getPosition();
                searchAddress();
            }
        });
        mAMap.setOnCameraChangeListener(new SimpleOnCameraChangeListener() {
            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                mLatLng = cameraPosition.target;
                searchAddress();
            }
        });

        mGeocoderSearch = new GeocodeSearch(this);
        mGeocoderSearch.setOnGeocodeSearchListener(new SimpleOnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int code) {
                if (code == 0 && regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null) {
                    refreshMarker(regeocodeResult.getRegeocodeAddress().getFormatAddress());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onStart() {
        super.onStart();

        PermissionUtil.request(this, REQUEST_CODE_LOCATION, new PermissionUtil.Delegate() {
            @Override
            public void onPermissionGranted() {
                LocationUtil.requestLocation(true, ChooseLocationDemo1Activity.this);
            }

            @Override
            public void onPermissionDenied() {
                ToastUtil.show("Some Permission is Denied");
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION:
                PermissionUtil.result(permissions, grantResults, new PermissionUtil.Delegate() {
                    @Override
                    public void onPermissionGranted() {
                        LocationUtil.requestLocation(true, ChooseLocationDemo1Activity.this);
                    }

                    @Override
                    public void onPermissionDenied() {
                        ToastUtil.show("Some Permission is Denied");
                    }
                });
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 该方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapMv.onResume();
    }

    /**
     * 该方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mMapMv.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * 该方法必须重写
     */
    @Override
    protected void onPause() {
        mMapMv.onPause();
        LocationUtil.stopLocation();
        super.onPause();
    }

    /**
     * 该方法必须重写
     */
    @Override
    protected void onDestroy() {
        mMapMv.onDestroy();
        LocationUtil.onDestroy();
        super.onDestroy();
    }

    private void refreshMap() {
//        mAMap.moveCamera(new CameraUpdateFactory().newLatLngZoom(mLatLng, 18));

        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, 18, 0, 0)), 700, null);
    }

    private void searchAddress() {
        mGeocoderSearch.getFromLocationAsyn(new RegeocodeQuery(new LatLonPoint(mLatLng.latitude, mLatLng.longitude), 50, GeocodeSearch.AMAP));
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            Logger.i("定位成功");

            mLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

            refreshMap();
        }
    }

    private void refreshMarker(String address) {
        initMarker();
        mMarker.setTitle(address);
        mMarker.setPosition(mLatLng);
        if (mMarker.isInfoWindowShown()) {
            mMarker.hideInfoWindow();
        }
        mMarker.showInfoWindow();
    }

    private void initMarker() {
        if (mMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mLatLng);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_selected));
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
            mMarker = mAMap.addMarker(markerOptions);
        }
    }
}