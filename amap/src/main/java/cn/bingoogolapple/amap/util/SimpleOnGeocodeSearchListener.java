package cn.bingoogolapple.amap.util;

import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/15 下午8:58
 * 描述:
 */
public abstract class SimpleOnGeocodeSearchListener implements GeocodeSearch.OnGeocodeSearchListener {
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
    }
}
