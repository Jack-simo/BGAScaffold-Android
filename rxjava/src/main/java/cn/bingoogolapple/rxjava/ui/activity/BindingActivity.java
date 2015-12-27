package cn.bingoogolapple.rxjava.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.rxjava.R;
import rx.functions.Action1;

public class BindingActivity extends TitlebarActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_binding);
    }

    @Override
    protected void setListener() {
        RxView
                .clicks(getViewById(R.id.btn_binding_throttle))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Logger.i(TAG, "点击了按钮");
                    }
                });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("RxBinding学习笔记");
    }

    @Override
    public void onClick(View v) {
    }

}