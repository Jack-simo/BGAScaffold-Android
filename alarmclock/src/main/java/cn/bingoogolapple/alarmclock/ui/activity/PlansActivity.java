package cn.bingoogolapple.alarmclock.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.model.Plan;
import cn.bingoogolapple.alarmclock.presenter.PlansPresenter;
import cn.bingoogolapple.alarmclock.presenter.impl.PlansPresenterImpl;
import cn.bingoogolapple.alertcontroller.BGAAlertAction;
import cn.bingoogolapple.alertcontroller.BGAAlertController;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildCheckedChangeListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.basenote.util.AppManager;
import cn.bingoogolapple.basenote.util.CalendarUtil;
import cn.bingoogolapple.basenote.view.TitlebarActivity;
import cn.bingoogolapple.basenote.widget.Divider;
import cn.bingoogolapple.swipeitemlayout.BGASwipeItemLayout;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 上午11:55
 * 描述:计划列表界面
 */
public class PlansActivity extends TitlebarActivity<PlansPresenter> implements PlansPresenter.View, BGAOnItemChildClickListener, BGAOnItemChildCheckedChangeListener {
    private static final int REQUEST_CODE_ADD = 1;
    private static final int REQUEST_CODE_VIEW = 2;
    private RecyclerView mPlanRv;
    private PlanAdapter mPlanAdapter;
    private int mCurrentSelectedPosition;

    private BGAAlertController mDeleteAlert;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_plans);
        mPlanRv = getViewById(R.id.rv_plans_plan);
    }

    @Override
    protected void setListener() {
        mPlanAdapter = new PlanAdapter(mPlanRv);
        mPlanAdapter.setOnItemChildClickListener(this);
        mPlanAdapter.setOnItemChildCheckedChangeListener(this);

        mPlanRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        hiddenLeftCtv();
        setTitle(R.string.app_name);
        setRightDrawable(R.mipmap.add_normal);

        mPlanRv.setLayoutManager(new LinearLayoutManager(this));
        mPlanRv.addItemDecoration(new Divider(this));
        mPlanRv.setAdapter(mPlanAdapter);

        mPresenter = new PlansPresenterImpl(this);
        mPresenter.loadPlans();
    }

    @Override
    protected void onClickRight() {
        forward(EditPlanActivity.newIntent(this, null), REQUEST_CODE_ADD);
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
    public void onBackPressed() {
        AppManager.getInstance().exitWithDoubleClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADD) {
                mPlanAdapter.addFirstItem(EditPlanActivity.getPlan(data));
                mPlanRv.smoothScrollToPosition(0);
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
            mDeleteAlert.addAction(new BGAAlertAction(R.string.confirm, BGAAlertAction.AlertActionStyle.Destructive, new BGAAlertAction.Delegate() {
                @Override
                public void onClick() {
                    mPresenter.deletePlan(mPlanAdapter.getItem(mCurrentSelectedPosition));
                }
            }));
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

    private final class PlanAdapter extends BGARecyclerViewAdapter<Plan> {
        /**
         * 当前处于打开状态的item
         */
        private List<BGASwipeItemLayout> mOpenedSil = new ArrayList<>();
        /**
         * 是否忽略SwitchCompat选中状态的改变
         */
        private boolean mIsIgnoreChange = false;

        public PlanAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_plan);
        }

        @Override
        protected void setItemChildListener(BGAViewHolderHelper helper) {
            BGASwipeItemLayout swipeItemLayout = helper.getView(R.id.sil_item_plan_root);
            swipeItemLayout.setDelegate(new BGASwipeItemLayout.BGASwipeItemLayoutDelegate() {
                @Override
                public void onBGASwipeItemLayoutOpened(BGASwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeItemLayoutWithAnim();
                    mOpenedSil.add(swipeItemLayout);
                }

                @Override
                public void onBGASwipeItemLayoutClosed(BGASwipeItemLayout swipeItemLayout) {
                    mOpenedSil.remove(swipeItemLayout);
                }

                @Override
                public void onBGASwipeItemLayoutStartOpen(BGASwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeItemLayoutWithAnim();
                }
            });
            helper.setItemChildClickListener(R.id.rl_item_plan_container);
            helper.setItemChildClickListener(R.id.tv_item_plan_delete);
            helper.setItemChildCheckedChangeListener(R.id.switch_item_plan_status);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, Plan model) {
            helper.setText(R.id.tv_item_plan_content, model.content);
            helper.setText(R.id.tv_item_plan_time, CalendarUtil.formatDetailDisplayTime(model.time));

            // 填充item期间忽略SwitchCompat选中状态的改变
            mIsIgnoreChange = true;
            helper.setChecked(R.id.switch_item_plan_status, model.status == Plan.STATUS_NOT_HANDLE);
            mIsIgnoreChange = false;
        }

        public void closeOpenedSwipeItemLayoutWithAnim() {
            for (BGASwipeItemLayout sil : mOpenedSil) {
                sil.closeWithAnim();
            }
            mOpenedSil.clear();
        }

        public boolean isIgnoreChange() {
            return mIsIgnoreChange;
        }
    }
}