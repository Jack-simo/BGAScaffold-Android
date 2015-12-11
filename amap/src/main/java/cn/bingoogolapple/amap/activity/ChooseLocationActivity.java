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
import cn.bingoogolapple.amap.util.LocationUtil;
import cn.bingoogolapple.amap.util.SimpleOnCameraChangeListener;
import cn.bingoogolapple.amap.util.SimpleOnMarkerDragListener;
import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.titlebar.BGATitlebar;

public class ChooseLocationActivity extends TitlebarActivity implements AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener, AMapLocationListener {
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 82;
    private AMap mAMap;
    private MapView mMapMv;
    private Marker mMarker;
    private GeocodeSearch mGeocoderSearch;

    private double mLatitude;
    private double mLongitude;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choose_location);
        mMapMv = getViewById(R.id.mv_choose_location_map);
    }

    @Override
    protected void setListener() {
        mTitlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate());
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
        mAMap.setOnMapClickListener(this);
        mAMap.setOnMarkerDragListener(new SimpleOnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.hideInfoWindow();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mLatitude = marker.getPosition().latitude;
                mLongitude = marker.getPosition().longitude;

                searchAddress();
            }
        });
        mAMap.setOnCameraChangeListener(new SimpleOnCameraChangeListener() {
            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                mLatitude = cameraPosition.target.latitude;
                mLongitude = cameraPosition.target.longitude;

                searchAddress();
            }
        });

        mGeocoderSearch = new GeocodeSearch(this);
        mGeocoderSearch.setOnGeocodeSearchListener(this);
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapMv.onResume();

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
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add("WRITE_EXTERNAL_STORAGE");
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
                        ActivityCompat.requestPermissions(ChooseLocationActivity.this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                });
                return;
            }
            ActivityCompat.requestPermissions(ChooseLocationActivity.this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
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
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
        new AlertDialog.Builder(ChooseLocationActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void requestLocationData() {
        LocationUtil.requestLocation(true, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapMv.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        mMapMv.onPause();
        LocationUtil.stopLocation();
        super.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        mMapMv.onDestroy();
        LocationUtil.onDestroy();
        super.onDestroy();
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

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            Logger.i(TAG, "定位成功");

            mLatitude = aMapLocation.getLatitude();
            mLongitude = aMapLocation.getLongitude();

            refreshMap();
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
        if (mMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(mLatitude, mLatitude));
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_selected));
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
            mMarker = mAMap.addMarker(markerOptions);
        }
    }
}