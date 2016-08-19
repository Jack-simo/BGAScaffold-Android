package cn.bingoogolapple.alarmclock.editplan;

import android.content.Context;
import android.content.Intent;
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
import cn.bingoogolapple.alarmclock.data.Plan;
import cn.bingoogolapple.alertcontroller.BGAAlertAction;
import cn.bingoogolapple.alertcontroller.BGAAlertController;
import cn.bingoogolapple.basenote.util.CalendarUtil;
import cn.bingoogolapple.basenote.util.KeyboardUtil;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.basenote.view.TitlebarActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 上午11:57
 * 描述:查看/添加/编辑界面
 */
public class EditPlanActivity extends TitlebarActivity<EditPlanPresenter> implements EditPlanPresenter.View, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
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

    private BGAAlertController mDeleteAlert;

    public static Intent newIntent(Context context, Plan plan) {
        Intent intent = new Intent(context, EditPlanActivity.class);
        intent.putExtra(EXTRA_PLAN, plan);
        return intent;
    }

    public static Plan getPlan(Intent intent) {
        return intent.getParcelableExtra(EXTRA_PLAN);
    }

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_plan);
        mTimeTv = getViewById(R.id.tv_edit_plan_time);
        mContentEt = getViewById(R.id.et_edit_plan_content);
    }

    @Override
    protected void setListener() {
        mTimeTv.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mUltimateCalendar = CalendarUtil.getZeroSecondCalendar();

        mPlan = getIntent().getParcelableExtra(EXTRA_PLAN);
        if (mPlan == null) {
            mOperateType = OPERATE_TYPE_ADD;
            setTitle(R.string.add_plan);
            setRightDrawable(R.mipmap.confirm_normal);
        } else {
            changeToView();
        }

        mPresenter = new EditPlanPresenterImpl(this);
    }

    @Override
    protected void onClickRight() {
        switch (mOperateType) {
            case OPERATE_TYPE_ADD:
                addPlan();
                break;
            case OPERATE_TYPE_VIEW:
                showMoreMenu();
                break;
            case OPERATE_TYPE_EDIT:
                editPlan();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_edit_plan_time) {
            KeyboardUtil.closeKeyboard(this);
            showDatePickerDialog();
        }
    }

    private void showDatePickerDialog() {
        mTempCalendar = CalendarUtil.getZeroSecondCalendar();
        if (mUltimateCalendar.getTimeInMillis() > mTempCalendar.getTimeInMillis()) {
            mTempCalendar.setTimeInMillis(mUltimateCalendar.getTimeInMillis());
        }
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, mTempCalendar.get(Calendar.YEAR), mTempCalendar.get(Calendar.MONTH), mTempCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePickerDialog.setMinDate(CalendarUtil.getCalendar());
        datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mTempCalendar.set(year, monthOfYear, dayOfMonth);
        showTimePickerDialog();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, mTempCalendar.get(Calendar.HOUR_OF_DAY), mTempCalendar.get(Calendar.MINUTE), false);
        timePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
        timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        mTempCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mTempCalendar.set(Calendar.MINUTE, minute);
        mTempCalendar.set(Calendar.SECOND, second);
        mUltimateCalendar.setTimeInMillis(mTempCalendar.getTimeInMillis());
        setTimeText();
    }

    private void changeToView() {
        mOperateType = OPERATE_TYPE_VIEW;
        setTitle(R.string.view_plan);
        setRightDrawable(R.mipmap.more_normal);
        mTimeTv.setEnabled(false);
        mContentEt.setEnabled(false);

        mUltimateCalendar.setTimeInMillis(mPlan.time);

        setTimeText();
        mContentEt.setText(mPlan.content);
    }

    private void setTimeText() {
        mTimeTv.setText(CalendarUtil.formatDetailDisplayTime(mUltimateCalendar.getTimeInMillis()));
    }

    private void changeToEdit() {
        mOperateType = OPERATE_TYPE_EDIT;
        setTitle(R.string.edit_plan);
        setRightDrawable(R.mipmap.confirm_normal);
        mTimeTv.setEnabled(true);
        mContentEt.setEnabled(true);
        mContentEt.setSelection(mContentEt.getText().toString().length());
    }

    @Override
    protected void showMoreMenu() {
        if (mMoreMenu == null) {
            mMoreMenu = new BGAAlertController(this, null, null, BGAAlertController.AlertControllerStyle.ActionSheet);
            mMoreMenu.addAction(new BGAAlertAction(R.string.edit, BGAAlertAction.AlertActionStyle.Default, new BGAAlertAction.Delegate() {
                @Override
                public void onClick() {
                    changeToEdit();
                }
            }));
            mMoreMenu.addAction(new BGAAlertAction(R.string.delete, BGAAlertAction.AlertActionStyle.Destructive, new BGAAlertAction.Delegate() {
                @Override
                public void onClick() {
                    showDeleteAlert();
                }
            }));
            mMoreMenu.addAction(new BGAAlertAction(R.string.cancel, BGAAlertAction.AlertActionStyle.Cancel, null));
        }
        mMoreMenu.show();
    }

    private void showDeleteAlert() {
        if (mDeleteAlert == null) {
            mDeleteAlert = new BGAAlertController(this, R.string.tip, R.string.tip_confirm_delete_plan, BGAAlertController.AlertControllerStyle.Alert);
            mDeleteAlert.addAction(new BGAAlertAction(R.string.confirm, BGAAlertAction.AlertActionStyle.Destructive, new BGAAlertAction.Delegate() {
                @Override
                public void onClick() {
                    mPresenter.deletePlan(mPlan);
                }
            }));
            mDeleteAlert.addAction(new BGAAlertAction(R.string.cancel, BGAAlertAction.AlertActionStyle.Cancel, null));
        }
        mDeleteAlert.show();
    }

    private void addPlan() {
        final String content = mContentEt.getText().toString().trim();
        if (validationPlain(content)) {
            KeyboardUtil.closeKeyboard(this);
            mPlan = new Plan();
            mPlan.content = content;
            mPlan.time = mUltimateCalendar.getTimeInMillis();
            mPlan.status = Plan.STATUS_NOT_HANDLE;

            mPresenter.addPlan(mPlan);
        }
    }

    private void editPlan() {
        final String content = mContentEt.getText().toString().trim();
        if (validationPlain(content)) {
            KeyboardUtil.closeKeyboard(this);

            mPresenter.updatePlan(mPlan, mUltimateCalendar.getTimeInMillis(), content);
        }
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
    public void addOrUpdateSuccess() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PLAN, mPlan);
        setResult(RESULT_OK, intent);
        backward();
    }

    @Override
    public void deleteSuccess() {
        setResult(RESULT_OK);
        backward();
    }
}