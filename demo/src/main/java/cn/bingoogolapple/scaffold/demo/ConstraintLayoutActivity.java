package cn.bingoogolapple.scaffold.demo;

import android.os.Bundle;

import cn.bingoogolapple.scaffold.util.ToastUtil;
import cn.bingoogolapple.scaffold.view.MvcActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午5:53
 * 描述:
 */
public class ConstraintLayoutActivity extends MvcActivity {

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_constraint_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected void setListener() {
        setOnClick(R.id.cl_demo15_three, view -> ToastUtil.show("测试 Placeholder"));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }
}