package cn.bingoogolapple.scaffolding.demo.adapters;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/13 下午3:54
 * 描述:
 */
public class ImageViewBindingAdapter {

    private static String getPath(String path) {
        if (path == null) {
            path = "";
        }

        if (!path.startsWith("http") && !path.startsWith("file")) {
            path = "file://" + path;
        }
        return path;
    }

    @BindingAdapter({"path", "placeholder"})
    public static void displayImage(ImageView imageView, String path, Drawable placeholder) {
        Glide.with(imageView.getContext())
                .load(getPath(path))
                .apply(RequestOptions.placeholderOf(placeholder).error(placeholder).dontAnimate())
                .into(imageView);
    }

}
