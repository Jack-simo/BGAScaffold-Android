package cn.bingoogolapple.bottomnavigation.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import cn.bingoogolapple.bottomnavigation.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/7/3 下午8:29
 * 描述:
 */
public class MessageFragment extends BaseMainFragment {
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_message);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        hiddenLeftCtv();
        setTitle(R.string.message);
        mTitlebar.setRightText(R.string.start_chat);

    }

    @Override
    protected void onClickRight() {
        Intent intent = new Intent();
        Uri uri = new Uri.Builder()
                .scheme("bga")
                .authority("www.bingoogolapple.cn")
                .path("/path1/path2")
                .appendPath("path3")
                .query("param1=param1value&param2=param2value") // 这种方式传递后通过getQueryParameter方法拿不到参数,只能通过getQuery方法获取到参数
                .appendQueryParameter("param3", "param3value")
                .build();
        intent.setData(uri);

        try {
            // 这里try一下,避免ActivityNotFoundException导致应用闪退
            mActivity.forward(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}