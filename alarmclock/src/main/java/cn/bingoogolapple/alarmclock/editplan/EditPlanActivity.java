package cn.bingoogolapple.alarmclock.editplan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.data.Plan;
import cn.bingoogolapple.alarmclock.databinding.ActivityEditPlanBinding;
import cn.bingoogolapple.alertcontroller.BGAAlertAction;
import cn.bingoogolapple.alertcontroller.BGAAlertController;
import cn.bingoogolapple.scaffolding.util.CalendarUtil;
import cn.bingoogolapple.scaffolding.util.KeyboardUtil;
import cn.bingoogolapple.scaffolding.util.ToastUtil;
import cn.bingoogolapple.scaffolding.view.MvpBindingActivity;
import cn.bingoogolapple.titlebar.BGATitlebar;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 上午11:57
 * 描述:查看/添加/编辑界面
 */
public class EditPlanActivity extends MvpBindingActivity<ActivityEditPlanBinding, EditPlanPresenter> implements EditPlanPresenter.View, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_PLAN = "EXTRA_PLAN";
    public static final int OPERATE_TYPE_ADD = 0;
    public static final int OPERATE_TYPE_VIEW = 1;
    public static final int OPERATE_TYPE_EDIT = 2;

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
    protected int getRootLayoutResID() {
        return R.layout.activity_edit_plan;
    }

    @Override
    protected void setListener() {
        mBinding.titleBar.setDelegate(new BGATitlebar.BGATitlebarDelegate() {
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
                        showMoreMenu();
                        break;
                    case OPERATE_TYPE_EDIT:
                        editPlan();
                        break;
                }
            }
        });
        setOnClick(mBinding.timeTv, object -> {
            KeyboardUtil.closeKeyboard(this);
            showDatePickerDialog();
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mBinding.titleBar.setLeftDrawable(getResources().getDrawable(R.mipmap.back_normal));
        mUltimateCalendar = CalendarUtil.getZeroSecondCalendar();

        mPlan = getIntent().getParcelableExtra(EXTRA_PLAN);
        if (mPlan == null) {
            mOperateType = OPERATE_TYPE_ADD;
            mBinding.titleBar.setTitleText(R.string.add_plan);
            mBinding.titleBar.setRightDrawable(getResources().getDrawable(R.mipmap.confirm_normal));
        } else {
            changeToView();
        }

        mPresenter = new EditPlanPresenterImpl(this);
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
        mBinding.titleBar.setTitleText(R.string.view_plan);
        mBinding.titleBar.setRightDrawable(getResources().getDrawable(R.mipmap.more_normal));
        mBinding.timeTv.setEnabled(false);
        mBinding.contentEt.setEnabled(false);

        mUltimateCalendar.setTimeInMillis(mPlan.time);

        setTimeText();
        mBinding.contentEt.setText(mPlan.content);
    }

    private void setTimeText() {
        mBinding.timeTv.setText(CalendarUtil.formatDetailDisplayTime(mUltimateCalendar.getTimeInMillis()));
    }

    private void changeToEdit() {
        mOperateType = OPERATE_TYPE_EDIT;
        mBinding.titleBar.setTitleText(R.string.edit_plan);

        mBinding.titleBar.setRightDrawable(getResources().getDrawable(R.mipmap.confirm_normal));
        mBinding.timeTv.setEnabled(true);
        mBinding.contentEt.setEnabled(true);
        mBinding.contentEt.setSelection(mBinding.contentEt.getText().toString().length());
    }

    @Override
    protected void showMoreMenu() {
        if (mMoreMenu == null) {
            mMoreMenu = new BGAAlertController(this, null, null, BGAAlertController.AlertControllerStyle.ActionSheet);
            mMoreMenu.addAction(new BGAAlertAction(R.string.edit, BGAAlertAction.AlertActionStyle.Default, () -> changeToEdit()));
            mMoreMenu.addAction(new BGAAlertAction(R.string.delete, BGAAlertAction.AlertActionStyle.Destructive, () -> showDeleteAlert()));
            mMoreMenu.addAction(new BGAAlertAction(R.string.cancel, BGAAlertAction.AlertActionStyle.Cancel, null));
        }
        mMoreMenu.show();
    }

    private void showDeleteAlert() {
        if (mDeleteAlert == null) {
            mDeleteAlert = new BGAAlertController(this, R.string.tip, R.string.tip_confirm_delete_plan, BGAAlertController.AlertControllerStyle.Alert);
            mDeleteAlert.addAction(new BGAAlertAction(R.string.confirm, BGAAlertAction.AlertActionStyle.Destructive, () -> mPresenter.deletePlan(mPlan)));
            mDeleteAlert.addAction(new BGAAlertAction(R.string.cancel, BGAAlertAction.AlertActionStyle.Cancel, null));
        }
        mDeleteAlert.show();
    }

    private void addPlan() {
        final String content = mBinding.contentEt.getText().toString().trim();
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
        final String content = mBinding.contentEt.getText().toString().trim();
        if (validationPlain(content)) {
            KeyboardUtil.closeKeyboard(this);

            mPresenter.updatePlan(mPlan, mUltimateCalendar.getTimeInMillis(), content);
        }
    }

    private boolean validationPlain(String content) {
        if (TextUtils.isEmpty(mBinding.timeTv.getText().toString().trim())) {
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