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
import cn.bingoogolapple.alarmclock.dao.PlanDao;
import cn.bingoogolapple.alarmclock.model.Plan;
import cn.bingoogolapple.alarmclock.util.AlarmUtil;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildCheckedChangeListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.CalendarUtil;
import cn.bingoogolapple.basenote.util.MinTimeRequestTask;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.basenote.widget.Divider;
import cn.bingoogolapple.swipeitemlayout.BGASwipeItemLayout;

public class MainActivity extends TitlebarActivity implements BGAOnItemChildClickListener, BGAOnItemChildCheckedChangeListener {
    private static final int REQUEST_CODE_ADD = 1;
    private static final int REQUEST_CODE_VIEW = 2;
    private RecyclerView mDataRv;
    private PlanAdapter mPlanAdapter;
    private int mCurrentViewPosition;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mDataRv = getViewById(R.id.rv_main_data);
    }

    @Override
    protected void setListener() {
        mPlanAdapter = new PlanAdapter(mDataRv);
        mPlanAdapter.setOnItemChildClickListener(this);
        mPlanAdapter.setOnItemChildCheckedChangeListener(this);

        mDataRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        mDataRv.setLayoutManager(new LinearLayoutManager(this));
        mDataRv.addItemDecoration(new Divider(this));
        mDataRv.setAdapter(mPlanAdapter);

        loadPlan();
    }

    @Override
    protected void onClickRight() {
        forward(EditActivity.newIntent(this, null), REQUEST_CODE_ADD);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (childView.getId() == R.id.tv_item_plan_delete) {
            deletePlan(position);
        } else if (childView.getId() == R.id.rl_item_plan_container) {
            mCurrentViewPosition = position;
            forward(EditActivity.newIntent(this, mPlanAdapter.getItem(mCurrentViewPosition)), REQUEST_CODE_VIEW);
        }
    }

    @Override
    public void onItemChildCheckedChanged(ViewGroup parent, CompoundButton childView, int position, boolean isChecked) {
        if (!mPlanAdapter.isIgnoreChange()) {
            updatePlanStatus(position);
        }
    }

    @Override
    public void onBackPressed() {
        mApp.exitWithDoubleClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADD) {
                mPlanAdapter.addFirstItem(EditActivity.getPlan(data));
                mDataRv.smoothScrollToPosition(0);
            } else if (requestCode == REQUEST_CODE_VIEW) {
                if (data == null) {
                    mPlanAdapter.removeItem(mCurrentViewPosition);
                } else {
                    mPlanAdapter.setItem(mCurrentViewPosition, EditActivity.getPlan(data));
                }
            }
        }
    }

    private void loadPlan() {
        new MinTimeRequestTask<Void, Void, List<Plan>>() {
            @Override
            protected void onPreExecute() {
                showLoadingDialog(R.string.loading);
            }

            @Override
            protected List<Plan> request(Void... params) {
                return PlanDao.queryPlan();
            }

            @Override
            protected void onPostExecute(List<Plan> plans) {
                dismissLoadingDialog();
                mPlanAdapter.setDatas(plans);
            }
        }.execute();
    }

    private void updatePlanStatus(final int position) {
        final Plan plan = mPlanAdapter.getItem(position);
        final int newStatus = plan.status == Plan.STATUS_ALREADY_HANDLE ? Plan.STATUS_NOT_HANDLE : Plan.STATUS_ALREADY_HANDLE;
        new MinTimeRequestTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                showLoadingDialog(R.string.loading);
            }

            @Override
            protected Boolean request(Void... params) {
                return PlanDao.updatePlan(plan.id, plan.time, plan.content, newStatus);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissLoadingDialog();
                if (result) {
                    plan.status = newStatus;
                    if (newStatus == Plan.STATUS_NOT_HANDLE && plan.time > CalendarUtil.getCalendar().getTimeInMillis()) {
                        AlarmUtil.addAlarm(plan);
                    } else {
                        AlarmUtil.cancelAlarm(plan);
                    }
                    mPlanAdapter.notifyItemChanged(position);
                } else {
                    ToastUtil.show(R.string.toast_update_plan_failure);
                }
            }
        }.execute();
    }

    private void deletePlan(final int position) {
        final Plan plan = mPlanAdapter.getItem(position);
        new MinTimeRequestTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                showLoadingDialog(R.string.loading);
            }

            @Override
            protected Boolean request(Void... voids) {
                return PlanDao.deletePlan(plan.id);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissLoadingDialog();
                if (result) {
                    AlarmUtil.cancelAlarm(plan);
                    mPlanAdapter.closeOpenedSwipeItemLayoutWithAnim();
                    mPlanAdapter.removeItem(position);
                } else {
                    ToastUtil.show(R.string.toast_delete_plan_failure);
                }
            }
        }.execute();
    }

    private final class PlanAdapter extends BGARecyclerViewAdapter<Plan> {
        /**
         * 当前处于打开状态的item
         */
        private List<BGASwipeItemLayout> mOpenedSil = new ArrayList<>();
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