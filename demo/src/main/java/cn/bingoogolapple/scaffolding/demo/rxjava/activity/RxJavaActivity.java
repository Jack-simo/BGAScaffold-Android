package cn.bingoogolapple.scaffolding.demo.rxjava.activity;

import android.os.Bundle;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.util.ToastUtil;
import cn.bingoogolapple.scaffolding.view.MvcActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午5:53
 * 描述:
 */
public class RxJavaActivity extends MvcActivity {

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_rxjava;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected void setListener() {
        setOnClick(R.id.btn_rxjava_test, o -> ToastUtil.show("test"));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }
}