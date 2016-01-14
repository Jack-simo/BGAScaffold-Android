package cn.bingoogolapple.basenote.util;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.ColorRes;

import com.zhy.changeskin.SkinManager;

import cn.bingoogolapple.basenote.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/13 上午1:05
 * 描述:
 */
public class SkinUtil {
    private static final String SP_KEY_SKIN = "SP_KEY_SKIN";
    private static final String SKIN_GREEN = "green";
    private static final String SKIN_ORANGE = "orange";

    private SkinUtil() {
    }

    public static void changeToGreen(Activity activity) {
        setStatusbarSkin(activity, R.color.skin_theme_background_green);
        SkinManager.getInstance().changeSkin(SKIN_GREEN);
        SPUtil.putString(SP_KEY_SKIN, SKIN_GREEN);
    }

    public static void changeToOrange(Activity activity) {
        setStatusbarSkin(activity, R.color.skin_theme_background_orange);
        SkinManager.getInstance().changeSkin(SKIN_ORANGE);
        SPUtil.putString(SP_KEY_SKIN, SKIN_ORANGE);
    }

    public static void toggleSkin(Activity activity) {
        if (!SKIN_ORANGE.equals(SPUtil.getString(SP_KEY_SKIN))) {
            changeToOrange(activity);
        } else {
            changeToGreen(activity);
        }
    }

    public static void initStatusbarSkin(Activity activity) {
        if (SKIN_ORANGE.equals(SPUtil.getString(SP_KEY_SKIN))) {
            setStatusbarSkin(activity, R.color.skin_theme_background_orange);
        } else {
            setStatusbarSkin(activity, R.color.skin_theme_background_green);
        }
    }

    public static void setStatusbarSkin(Activity activity, @ColorRes int colorResId) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = activity.getResources().getColor(colorResId);
            activity.getWindow().setStatusBarColor(color);
            activity.getWindow().setNavigationBarColor(color);
        }
    }


}