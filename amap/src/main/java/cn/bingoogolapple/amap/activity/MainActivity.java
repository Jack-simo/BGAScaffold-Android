package cn.bingoogolapple.amap.activity;

import android.os.Bundle;
import android.view.View;

import cn.bingoogolapple.amap.R;
import cn.bingoogolapple.basenote.activity.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.btn_main_net_location).setOnClickListener(this);
        getViewById(R.id.btn_main_multy_location).setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_main_net_location) {
            forward(NetLocationActivity.class);
        } else if (v.getId() == R.id.btn_main_multy_location) {
            forward(MultyLocationActivity.class);
        }
    }

}