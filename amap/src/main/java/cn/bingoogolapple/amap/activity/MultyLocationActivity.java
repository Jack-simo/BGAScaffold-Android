package cn.bingoogolapple.amap.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.amap.R;
import cn.bingoogolapple.amap.util.SimpleAMapLocationListener;
import cn.bingoogolapple.basenote.activity.BaseActivity;
import cn.bingoogolapple.basenote.util.ToastUtil;

public class MultyLocationActivity extends BaseActivity implements AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener {
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 82;
    private LocationManagerProxy mLocationManagerProxy;
    private AMap mAMap;
    private MapView mMapView;
    private boolean mIsAddedMarker;
    private Marker mMarker;
    private GeocodeSearch mGeocoderSearch;

    private double mLatitude;
    private double mLongitude;

    private SimpleAMapLocationListener mAMapLocationListener = new SimpleAMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
                mLatitude = aMapLocation.getLatitude();
                mLongitude = aMapLocation.getLongitude();

                refreshMap();
            }
        }
    };

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_multy_location);
        mMapView = getViewById(R.id.mapView);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        setUpMap();
    }

    private void setUpMap() {
        if (mAMap != null) {
            return;
        }

        mAMap = mMapView.getMap();
        mAMap.setOnMapClickListener(this);
        mAMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.hideInfoWindow();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mLatitude = marker.getPosition().latitude;
                mLongitude = marker.getPosition().longitude;

                searchAddress();
            }
        });
        mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                mLatitude = cameraPosition.target.latitude;
                mLongitude = cameraPosition.target.longitude;

                searchAddress();
            }

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }
        });

        mGeocoderSearch = new GeocodeSearch(this);
        mGeocoderSearch.setOnGeocodeSearchListener(this);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        if (mLatitude == 0 && mLongitude == 0) {
            requestLocationDataWrapper();
        } else {
            refreshMap();
        }

    }

    private void requestLocationDataWrapper() {
        List<String> permissionsNeeded = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();

        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsNeeded.add("ACCESS_FINE_LOCATION");
        }
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissionsNeeded.add("ACCESS_COARSE_LOCATION");
        }

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    message = message + ", " + permissionsNeeded.get(i);
                }
                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MultyLocationActivity.this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                });
                return;
            }
            ActivityCompat.requestPermissions(MultyLocationActivity.this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        requestLocationData();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    requestLocationData();
                } else {
                    ToastUtil.show("Some Permission is Denied");
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MultyLocationActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void requestLocationData() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, mAMapLocationListener);
        mLocationManagerProxy.setGpsEnable(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        stopLocation();
    }

    private void stopLocation() {
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(mAMapLocationListener);
            mLocationManagerProxy.destroy();
        }
        mLocationManagerProxy = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mLatitude = latLng.latitude;
        mLongitude = latLng.longitude;

        refreshMap();
    }

    private void refreshMap() {
//        mAMap.moveCamera(new CameraUpdateFactory().newLatLngZoom(new LatLng(mLatitude, mLongitude), 18));

        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(mLatitude, mLongitude), 18, 0, 0)), 700, null);
    }

    private void searchAddress() {
        mGeocoderSearch.getFromLocationAsyn(new RegeocodeQuery(new LatLonPoint(mLatitude, mLongitude), 50, GeocodeSearch.AMAP));
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int code) {
        if (code == 0 && regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null) {
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            refreshMarker(regeocodeAddress.getFormatAddress());
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int code) {
        if (code == 0 && geocodeResult != null && geocodeResult.getGeocodeAddressList() != null && geocodeResult.getGeocodeAddressList().size() > 0) {
            List<GeocodeAddress> addressList = geocodeResult.getGeocodeAddressList();
            GeocodeAddress geocodeAddress = addressList.get(0);
            refreshMarker(geocodeAddress.getFormatAddress());
        }
    }

    private void refreshMarker(String address) {
        initMarker();
        mMarker.setTitle(address);
        mMarker.setPosition(new LatLng(mLatitude, mLongitude));
        if (mMarker.isInfoWindowShown()) {
            mMarker.hideInfoWindow();
        }
        mMarker.showInfoWindow();
    }

    private void initMarker() {
        if (!mIsAddedMarker) {
            mIsAddedMarker = true;
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(mLatitude, mLatitude));
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
            mMarker = mAMap.addMarker(markerOptions);
        }
    }
}