package cn.bingoogolapple.alarmclock.plans;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.List;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.data.Plan;
import cn.bingoogolapple.alarmclock.databinding.ActivityPlansBinding;
import cn.bingoogolapple.alarmclock.editplan.EditPlanActivity;
import cn.bingoogolapple.alertcontroller.BGAAlertAction;
import cn.bingoogolapple.alertcontroller.BGAAlertController;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildCheckedChangeListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.scaffolding.view.MvpBindingActivity;
import cn.bingoogolapple.scaffolding.widget.Divider;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 上午11:55
 * 描述:计划列表界面
 */
public class PlansActivity extends MvpBindingActivity<ActivityPlansBinding, PlansPresenter> implements PlansPresenter.View, BGAOnItemChildClickListener, BGAOnItemChildCheckedChangeListener {
    private static final int REQUEST_CODE_ADD = 1;
    private static final int REQUEST_CODE_VIEW = 2;
    private PlanAdapter mPlanAdapter;
    private int mCurrentSelectedPosition;
    private BGAAlertController mDeleteAlert;

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_plans;
    }

    @Override
    protected void setListener() {
        mBinding.titleBar.setDelegate(this);
        mPlanAdapter = new PlanAdapter(mBinding.planRv);
        mPlanAdapter.setOnItemChildClickListener(this);
        mPlanAdapter.setOnItemChildCheckedChangeListener(this);

        mBinding.planRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {
                    mPlanAdapter.closeOpenedSwipeItemLayoutWithAnim();
                }
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mBinding.planRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.planRv.addItemDecoration(Divider.newBitmapDivider());
        mBinding.planRv.setAdapter(mPlanAdapter);

        mPresenter = new PlansPresenterImpl(this);
        mPresenter.loadPlans();
    }

    @Override
    public void onClickRightCtv() {
        forward(EditPlanActivity.newIntent(PlansActivity.this, null), REQUEST_CODE_ADD);
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        mCurrentSelectedPosition = position;
        if (childView.getId() == R.id.tv_item_plan_delete) {
            showDeleteAlert();
        } else if (childView.getId() == R.id.rl_item_plan_container) {
            forward(EditPlanActivity.newIntent(this, mPlanAdapter.getItem(mCurrentSelectedPosition)), REQUEST_CODE_VIEW);
        }
    }

    @Override
    public void onItemChildCheckedChanged(ViewGroup parent, CompoundButton childView, int position, boolean isChecked) {
        // 如果不忽略SwitchCompat选中状态的改变，则更新当前item
        if (!mPlanAdapter.isIgnoreChange()) {
            mPresenter.updatePlanStatus(position, mPlanAdapter.getItem(position));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADD) {
                mPlanAdapter.addFirstItem(EditPlanActivity.getPlan(data));
                mBinding.planRv.smoothScrollToPosition(0);
            } else if (requestCode == REQUEST_CODE_VIEW) {
                if (data == null) {
                    mPlanAdapter.removeItem(mCurrentSelectedPosition);
                } else {
                    mPlanAdapter.setItem(mCurrentSelectedPosition, EditPlanActivity.getPlan(data));
                }
            }
        }
    }

    private void showDeleteAlert() {
        if (mDeleteAlert == null) {
            mDeleteAlert = new BGAAlertController(this, R.string.tip, R.string.tip_confirm_delete_plan, BGAAlertController.AlertControllerStyle.Alert);
            mDeleteAlert.addAction(new BGAAlertAction(R.string.confirm, BGAAlertAction.AlertActionStyle.Destructive, () -> mPresenter.deletePlan(mPlanAdapter.getItem(mCurrentSelectedPosition))));
            mDeleteAlert.addAction(new BGAAlertAction(R.string.cancel, BGAAlertAction.AlertActionStyle.Cancel, null));
        }
        mDeleteAlert.show();
    }

    @Override
    public void showPlans(List<Plan> plans) {
        mPlanAdapter.setData(plans);
    }

    @Override
    public void notifyItemChanged(int position) {
        mPlanAdapter.notifyItemChanged(position);
    }

    @Override
    public void removeCurrentSelectedItem() {
        mPlanAdapter.closeOpenedSwipeItemLayoutWithAnim();
        mPlanAdapter.removeItem(mCurrentSelectedPosition);
    }
}