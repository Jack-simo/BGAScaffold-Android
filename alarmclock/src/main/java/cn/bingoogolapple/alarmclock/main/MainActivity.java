package cn.bingoogolapple.alarmclock.main;

import android.os.Bundle;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.plans.PlansActivity;
import cn.bingoogolapple.alarmclock.plans.PlansFragmentActivity;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.view.TitleBarActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午5:53
 * 描述:
 */
public class MainActivity extends TitleBarActivity {
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void setListener() {
        setOnClick(R.id.activity_tv, object -> forward(PlansActivity.class));
        setOnClick(R.id.fragment_tv, object -> forward(PlansFragmentActivity.class));
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle(R.string.app_name);
        mTitleBar.hiddenLeftCtv();
    }

    @Override
    public void onBackPressed() {
        AppManager.getInstance().exitWithDoubleClick();
    }
}