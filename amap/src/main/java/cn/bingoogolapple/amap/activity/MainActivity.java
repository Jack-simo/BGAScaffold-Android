package cn.bingoogolapple.amap.activity;

import android.os.Bundle;

import cn.bingoogolapple.amap.R;
import cn.bingoogolapple.basenote.activity.TitlebarActivity;

public class MainActivity extends TitlebarActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void setListener() {
        setOnClick(R.id.btn_main_choose_location_demo1, object -> forward(ChooseLocationDemo1Activity.class));
        setOnClick(R.id.btn_main_choose_location_demo2, object -> forward(ChooseLocationDemo2Activity.class));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        hiddenLeftCtv();
        setTitle("高德地图学习笔记");
    }
}