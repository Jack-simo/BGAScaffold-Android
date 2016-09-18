package cn.bingoogolapple.basenote.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import cn.bingoogolapple.basenote.App;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/20 下午11:30
 * 描述:
 */
public class SPUtil {
    private static SharedPreferences mSharedPreferences;

    private SPUtil() {
    }

    private static SharedPreferences getPreferences() {
        if (mSharedPreferences == null) {
            synchronized (SPUtil.class) {
                if (mSharedPreferences == null) {
                    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
                }
            }
        }
        return mSharedPreferences;
    }

    public static void clear() {
        getPreferences().edit().clear().apply();
    }

    public static void putString(String key, String value) {
        getPreferences().edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return getPreferences().getString(key, null);
    }

    public static void putInt(String key, int value) {
        getPreferences().edit().putInt(key, value).apply();
    }

    public static int getInt(String key) {
        return getPreferences().getInt(key, 0);
    }

    public static void putBoolean(String key, Boolean value) {
        getPreferences().edit().putBoolean(key, value).apply();
    }

    public static void putLong(String key, long value) {
        getPreferences().edit().putLong(key, value).apply();
    }

    public static long getLong(String key) {
        return getPreferences().getLong(key, 0);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static void remove(String key) {
        getPreferences().edit().remove(key).apply();
    }

    public static boolean hasKey(String key) {
        return getPreferences().contains(key);
    }
}