package cn.bingoogolapple.scaffolding.adapter;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.bingoogolapple.scaffolding.util.BGAOnNoDoubleClickListener;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/13 上午10:21
 * 描述:
 */
public class BGABindingAdapter {

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
        Glide.with(imageView.getContext()).load(getPath(path)).placeholder(placeholder).into(imageView);
    }

    @BindingAdapter({"onNoDoubleClick"})
    public static void onNoDoubleClick(View view, View.OnClickListener onClickListener) {
        view.setOnClickListener(new BGAOnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onClickListener.onClick(v);
            }
        });
    }
}