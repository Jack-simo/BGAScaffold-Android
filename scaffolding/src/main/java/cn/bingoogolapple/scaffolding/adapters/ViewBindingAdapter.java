package cn.bingoogolapple.scaffolding.adapters;

import android.databinding.BindingAdapter;
import android.view.View;

import cn.bingoogolapple.scaffolding.util.BGAOnNoDoubleClickListener;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/13 下午3:56
 * 描述:
 */
public class ViewBindingAdapter {

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
