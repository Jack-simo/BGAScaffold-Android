package cn.bingoogolapple.basenote.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/12 下午11:45
 * 描述:
 */
public class DrawableUtil {

    private DrawableUtil() {
    }

    public static Drawable tintDrawable(Context context, Drawable drawable, @ColorRes int color) {
        if (drawable == null) {
            return null;
        }

        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, context.getResources().getColor(color));
        return wrappedDrawable;
    }

    public static void tintPressedIndicator(ImageView imageView, @DrawableRes int normalResId, @DrawableRes int pressedResId, @ColorRes int colorResId) {
        Drawable normal = imageView.getResources().getDrawable(normalResId);
        Drawable pressed = imageView.getResources().getDrawable(pressedResId);
        pressed = tintDrawable(imageView.getContext(), pressed, colorResId);
        imageView.setImageDrawable(getPressedSelectorDrawable(normal, pressed));
    }

    public static StateListDrawable getPressedSelectorDrawable(Drawable normal, Drawable pressed) {
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        bg.addState(new int[]{android.R.attr.state_enabled}, normal);
        bg.addState(new int[]{}, normal);
        return bg;
    }

}
