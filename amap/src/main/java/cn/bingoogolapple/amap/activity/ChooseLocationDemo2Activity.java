package cn.bingoogolapple.amap.activity;

import android.os.Bundle;
import android.view.View;

import cn.bingoogolapple.amap.R;
import cn.bingoogolapple.basenote.activity.TitlebarActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/15 下午7:48
 * 描述:
 */
public class ChooseLocationDemo2Activity extends TitlebarActivity {
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_choose_location_demo2);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("选择地理位置");
    }

    @Override
    public void onClick(View v) {

    }
}
