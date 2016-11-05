package cn.bingoogolapple.scaffolding.util;

import android.content.Context;

import java.io.Serializable;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/14 下午12:35
 * 描述:
 */
public class CacheManager {
    private CacheManager() {
    }

    public static boolean saveObject(Context context, Serializable serializable, String key) {
        return true;
    }

    public static Serializable readObject(Context context, String key, final long expireTime) {
        return null;
    }
}