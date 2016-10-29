package cn.bingoogolapple.rxjava.ui.activity;

import android.os.Bundle;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.rxjava.R;

public class RxBusActivity extends TitlebarActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_rxbus);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("RxBus学习笔记");
    }
}