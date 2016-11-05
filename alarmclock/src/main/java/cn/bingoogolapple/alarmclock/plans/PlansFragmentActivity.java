package cn.bingoogolapple.alarmclock.plans;

import android.os.Bundle;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.scaffolding.view.BaseActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午7:52
 * 描述:
 */
public class PlansFragmentActivity extends BaseActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_fragment_plans);
        getSupportFragmentManager().beginTransaction().add(R.id.content_fl, new PlansFragment()).commit();
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
