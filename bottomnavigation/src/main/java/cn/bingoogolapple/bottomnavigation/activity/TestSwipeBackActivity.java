package cn.bingoogolapple.bottomnavigation.activity;

import android.os.Bundle;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.bottomnavigation.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/12 下午11:25
 * 描述:
 */
public class TestSwipeBackActivity extends TitlebarActivity {

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_test_swipe_back);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setLeftDrawable(R.drawable.selector_nav_back);
        setTitle("测试欢动返回");
    }
}
