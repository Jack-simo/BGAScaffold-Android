package cn.bingoogolapple.alarmclock.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.data.Plan;
import cn.bingoogolapple.alarmclock.databinding.ActivityEditPlanBinding;
import cn.bingoogolapple.basenote.presenter.BasePresenter;
import cn.bingoogolapple.basenote.util.CalendarUtil;
import cn.bingoogolapple.basenote.view.BaseMvvmActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/19 上午12:27
 * 描述:闹钟提示界面
 */
public class AlarmActivity extends BaseMvvmActivity<ActivityEditPlanBinding, BasePresenter> {
    public static final String EXTRA_PLAN = "EXTRA_PLAN";

    public static Intent newIntent(Context context, Plan plan) {
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.putExtra(EXTRA_PLAN, plan);
        return intent;
    }

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_edit_plan;
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mBinding.titleBar.setTitleText(R.string.view_plan);

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Plan plan = getIntent().getParcelableExtra(EXTRA_PLAN);
        mBinding.timeTv.setEnabled(false);
        mBinding.timeTv.setEnabled(false);

        mBinding.timeTv.setText(plan.content);
        mBinding.timeTv.setText(CalendarUtil.formatDetailDisplayTime(plan.time));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}