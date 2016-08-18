package cn.bingoogolapple.alarmclock.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.model.Plan;
import cn.bingoogolapple.basenote.presenter.BasePresenter;
import cn.bingoogolapple.basenote.util.CalendarUtil;
import cn.bingoogolapple.basenote.view.TitlebarActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/19 上午12:27
 * 描述:闹钟提示界面
 */
public class AlarmActivity extends TitlebarActivity<BasePresenter> {
    public static final String EXTRA_PLAN = "EXTRA_PLAN";
    private AppCompatTextView mTimeTv;
    private AppCompatEditText mContentEt;

    public static Intent newIntent(Context context, Plan plan) {
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.putExtra(EXTRA_PLAN, plan);
        return intent;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_plan);
        mTimeTv = getViewById(R.id.tv_edit_plan_time);
        mContentEt = getViewById(R.id.et_edit_plan_content);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        hiddenLeftCtv();
        setTitle(R.string.view_plan);

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Plan plan = getIntent().getParcelableExtra(EXTRA_PLAN);
        mTimeTv.setEnabled(false);
        mContentEt.setEnabled(false);

        mContentEt.setText(plan.content);
        mTimeTv.setText(CalendarUtil.formatDetailDisplayTime(plan.time));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}