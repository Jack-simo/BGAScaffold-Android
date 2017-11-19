package cn.bingoogolapple.scaffolding.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2017/11/19
 * 描述:
 */
public class GsonUtil {
    private Gson mGson;

    private GsonUtil() {
        mGson = new Gson();
    }

    private static class SingletonHolder {
        private static final GsonUtil INSTANCE = new GsonUtil();
    }

    public static GsonUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Gson getGson() {
        return mGson;
    }

    public static String toJson(Object src) {
        return getInstance().getGson().toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return getInstance().getGson().fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return getInstance().getGson().fromJson(json, typeOfT);
    }
}
