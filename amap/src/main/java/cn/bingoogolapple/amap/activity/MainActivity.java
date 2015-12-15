package cn.bingoogolapple.amap.activity;

import android.os.Bundle;
import android.view.View;

import cn.bingoogolapple.amap.R;
import cn.bingoogolapple.basenote.activity.TitlebarActivity;

public class MainActivity extends TitlebarActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.btn_main_choose_location_demo1).setOnClickListener(this);
        getViewById(R.id.btn_main_choose_location_demo2).setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        hiddenLeftCtv();
        setTitle("高德地图学习笔记");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_main_choose_location_demo1) {
            forward(ChooseLocationDemo1Activity.class);
        } else if (v.getId() == R.id.btn_main_choose_location_demo2) {
            forward(ChooseLocationDemo2Activity.class);
        }
    }

}