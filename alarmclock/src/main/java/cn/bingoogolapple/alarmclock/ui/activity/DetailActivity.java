package cn.bingoogolapple.alarmclock.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.dao.PlanDao;
import cn.bingoogolapple.alarmclock.model.Plan;
import cn.bingoogolapple.basenote.activity.BaseActivity;
import cn.bingoogolapple.basenote.util.CalendarUtil;
import cn.bingoogolapple.basenote.util.KeyboardUtil;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.titlebar.BGATitlebar;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 上午11:57
 * 描述:
 */
public class DetailActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_OPERATE_TYPE = "EXTRA_OPERATE_TYPE";
    public static final String EXTRA_PLAN = "EXTRA_PLAN";
    public static final int OPERATE_TYPE_ADD = 0;
    public static final int OPERATE_TYPE_VIEW = 1;
    public static final int OPERATE_TYPE_EDIT = 2;

    private AppCompatTextView mTimeTv;
    private AppCompatEditText mContentEt;
    private int mOperateType = OPERATE_TYPE_ADD;
    private Plan mPlan;
    private Calendar mUltimateCalendar;
    private Calendar mTempCalendar;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_detail);
        mTitlebar = getViewById(R.id.titlebar);
        mTimeTv = getViewById(R.id.tv_detail_time);
        mContentEt = getViewById(R.id.et_detail_content);
    }

    @Override
    protected void setListener() {
        mTitlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate() {
            @Override
            public void onClickLeftCtv() {
                onBackPressed();
            }

            @Override
            public void onClickRightCtv() {
                switch (mOperateType) {
                    case OPERATE_TYPE_ADD:
                        addPlan();
                        break;
                    case OPERATE_TYPE_VIEW:
                        changeToEdit();
                        break;
                    case OPERATE_TYPE_EDIT:
                        editPlan();
                        break;
                }
            }
        });

        mTimeTv.setOnClickListener(this);
    }


    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mOperateType = getIntent().getIntExtra(EXTRA_OPERATE_TYPE, OPERATE_TYPE_ADD);

        mUltimateCalendar = CalendarUtil.getZeroSecondCalendar();
        if (mOperateType == OPERATE_TYPE_VIEW) {
            changeToView();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_detail_time) {
            KeyboardUtil.closeKeyboard(this);
            showDatePickerDialog();
        }
    }

    private void showDatePickerDialog() {
        mTempCalendar = CalendarUtil.getZeroSecondCalendar();
        DatePickerDialog dpd = DatePickerDialog.newInstance(this, mTempCalendar.get(Calendar.YEAR), mTempCalendar.get(Calendar.MONTH), mTempCalendar.get(Calendar.DAY_OF_MONTH));
        dpd.setAccentColor(getResources().getColor(R.color.orange_pressed));
        int thisYear = mTempCalendar.get(Calendar.YEAR);
        dpd.setYearRange(thisYear - 5, thisYear + 5);
        dpd.show(getFragmentManager(), "DatePickerDialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mTempCalendar.set(year, monthOfYear, dayOfMonth);
        showTimePickerDialog();
    }

    private void showTimePickerDialog() {
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, mTempCalendar.get(Calendar.HOUR), mTempCalendar.get(Calendar.MINUTE), true);
        tpd.setAccentColor(getResources().getColor(R.color.orange_pressed));
        tpd.show(getFragmentManager(), "TimePickerDialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mTempCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mTempCalendar.set(Calendar.MINUTE, minute);
        mUltimateCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis());
        setTimeText();
    }

    private void changeToView() {
        mTitlebar.setTitleText(R.string.view_plan);
        mTitlebar.setRightText(R.string.edit);
        mTimeTv.setEnabled(false);
        mContentEt.setEnabled(false);
        mPlan = getIntent().getParcelableExtra(EXTRA_PLAN);

        mUltimateCalendar.setTimeInMillis(mPlan.time);

        setTimeText();
        mContentEt.setText(mPlan.content);
    }

    private void setTimeText() {
        mTimeTv.setText(CalendarUtil.formatDisplayTime(mUltimateCalendar.getTimeInMillis()));
    }

    private void changeToEdit() {
        mOperateType = OPERATE_TYPE_EDIT;
        mTitlebar.setTitleText(R.string.edit_plan);
        mTitlebar.setRightText(R.string.finish);
        mTimeTv.setEnabled(true);
        mContentEt.setEnabled(true);
        mContentEt.setSelection(mContentEt.getText().toString().length());
    }

    private void addPlan() {
        final String content = mContentEt.getText().toString().trim();
        if (validationPlain(content)) {
            KeyboardUtil.closeKeyboard(this);
            mPlan = new Plan();
            mPlan.content = content;
            mPlan.time = mUltimateCalendar.getTimeInMillis();
            mPlan.status = Plan.STATUS_NOT_HANDLE;

            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected void onPreExecute() {
                    showLoadingDialog(R.string.loading);
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    long beginTime = System.currentTimeMillis();
                    boolean result = PlanDao.insertPlan(mPlan);
                    long time = System.currentTimeMillis() - beginTime;
                    if (time < DELAY_TIME) {
                        try {
                            Thread.sleep(DELAY_TIME - time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    dismissLoadingDialog();
                    if (result) {
                        backwardSuccess();
                    } else {
                        ToastUtil.show(R.string.toast_add_plan_failure);
                    }
                }
            }.execute();
        }
    }

    private void editPlan() {
        final String content = mContentEt.getText().toString().trim();
        if (validationPlain(content)) {
            KeyboardUtil.closeKeyboard(this);
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected void onPreExecute() {
                    showLoadingDialog(R.string.loading);
                }

                @Override
                protected Boolean doInBackground(Void... params) {
                    long beginTime = System.currentTimeMillis();
                    boolean result = PlanDao.updatePlan(mPlan.id, mUltimateCalendar.getTimeInMillis(), content, Plan.STATUS_NOT_HANDLE);
                    long time = System.currentTimeMillis() - beginTime;
                    if (time < DELAY_TIME) {
                        try {
                            Thread.sleep(DELAY_TIME - time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    dismissLoadingDialog();
                    if (result) {
                        mPlan.time = mUltimateCalendar.getTimeInMillis();
                        mPlan.content = content;
                        mPlan.status = Plan.STATUS_NOT_HANDLE;

                        backwardSuccess();
                    } else {
                        ToastUtil.show(R.string.toast_update_plan_failure);
                    }
                }
            }.execute();
        }
    }

    private void backwardSuccess() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PLAN, mPlan);
        setResult(RESULT_OK, intent);
        backward();
    }

    private boolean validationPlain(String content) {
        if (TextUtils.isEmpty(mTimeTv.getText().toString().trim())) {
            ToastUtil.show(R.string.toast_plan_time_invalid_empty);
            return false;
        }
        if (mUltimateCalendar.before(CalendarUtil.getCalendar())) {
            ToastUtil.show(R.string.toast_plan_time_invalid);
            return false;
        }
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show(R.string.toast_plan_content_invalid_empty);
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        backward();
    }
}