package cn.bingoogolapple.amap.activity;

import android.Manifest;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.List;

import cn.bingoogolapple.amap.R;
import cn.bingoogolapple.amap.util.LocationUtil;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.PermissionUtil;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.basenote.widget.Divider;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/15 下午7:48
 * 描述:
 */
public class ChooseLocationDemo2Activity extends TitlebarActivity implements AMapLocationListener, BGAOnRVItemClickListener, PoiSearch.OnPoiSearchListener {
    private static final int REQUEST_CODE_LOCATION = 1;
    private MapView mMapMv;
    private AMap mAMap;
    private RecyclerView mContentRv;
    private LocationAdapter mLocationAdapter;
    private Marker mMarker;
    private PoiItem mCurrentPoiItem;

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choose_location_demo2);
        mMapMv = getViewById(R.id.mv_choose_location_demo2_map);
        mContentRv = getViewById(R.id.rv_choose_location_demo2_location);
    }

    @Override
    protected void setListener() {
        mLocationAdapter = new LocationAdapter(mContentRv);
        mLocationAdapter.setOnRVItemClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("选择地理位置");

        mMapMv.onCreate(savedInstanceState);

        setUpMap();

        mContentRv.setLayoutManager(new LinearLayoutManager(this));
        mContentRv.addItemDecoration(new Divider(this));
        mContentRv.setAdapter(mLocationAdapter);
    }

    private void setUpMap() {
        mAMap = mMapMv.getMap();
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        mAMap.setMyLocationEnabled(true);
        mAMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                requestLocation();
            }

            @Override
            public void deactivate() {
            }
        });
    }

    private void requestLocation() {
        showLoadingDialog(R.string.loading);
        PermissionUtil.request(this, REQUEST_CODE_LOCATION, new PermissionUtil.Delegate() {
            @Override
            public void onPermissionGranted() {
                LocationUtil.requestLocation(true, ChooseLocationDemo2Activity.this);
            }

            @Override
            public void onPermissionDenied() {
                dismissLoadingDialog();
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
                        LocationUtil.requestLocation(true, ChooseLocationDemo2Activity.this);
                    }

                    @Override
                    public void onPermissionDenied() {
                        dismissLoadingDialog();
                        ToastUtil.show("Some Permission is Denied");
                    }
                });
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestLocation();
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

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            LocationUtil.stopLocation();

            LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            mCurrentPoiItem = new PoiItem(null, new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude()), aMapLocation.getAddress(), null);

            PoiSearch.Query query = new PoiSearch.Query("*", "汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施", aMapLocation.getCityCode());
            query.setPageSize(50);
            query.setPageNum(0);
            PoiSearch poiSearch = new PoiSearch(ChooseLocationDemo2Activity.this, query);
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latLng.latitude, latLng.longitude), 1000));
            poiSearch.setOnPoiSearchListener(ChooseLocationDemo2Activity.this);
            poiSearch.searchPOIAsyn();

            mAMap.animateCamera(new CameraUpdateFactory().newLatLngZoom(latLng, 18));
            if (mMarker == null) {
                mMarker = mAMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.pin_normal))).zIndex(1));
            } else {
                mMarker.setPosition(latLng);
            }
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        dismissLoadingDialog();

        List<PoiItem> poiItems = poiResult.getPois();
        poiItems.add(0, mCurrentPoiItem);
        mLocationAdapter.setData(poiItems);
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int position) {
        ToastUtil.show(getAddress(mLocationAdapter.getItem(position)));
    }

    private static class LocationAdapter extends BGARecyclerViewAdapter<PoiItem> {

        public LocationAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_title_desc);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, PoiItem model) {
            if (position == 0) {
                helper.setText(R.id.tv_item_title_desc_title, "[当前位置]" + model.getTitle());
            } else {
                helper.setText(R.id.tv_item_title_desc_title, model.getTitle());
            }
            helper.setText(R.id.tv_item_title_desc_desc, getAddress(model));
        }
    }

    public static String getAddress(PoiItem poiItem) {
        return TextUtils.isEmpty(poiItem.getPoiId()) ? poiItem.getTitle() : poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName() + poiItem.getTitle();
    }
}