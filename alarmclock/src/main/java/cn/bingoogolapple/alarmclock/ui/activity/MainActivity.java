package cn.bingoogolapple.alarmclock.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.dao.PlanDao;
import cn.bingoogolapple.alarmclock.model.Plan;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.basenote.activity.BaseActivity;
import cn.bingoogolapple.basenote.util.CalendarUtil;
import cn.bingoogolapple.basenote.widget.Divider;
import cn.bingoogolapple.swipeitemlayout.BGASwipeItemLayout;
import cn.bingoogolapple.titlebar.BGATitlebar;

public class MainActivity extends BaseActivity implements BGAOnItemChildClickListener {
    private static final int REQUEST_CODE_ADD = 1;
    private static final int REQUEST_CODE_VIEW = 2;
    private RecyclerView mDataRv;
    private PlanAdapter mPlanAdapter;
    private int mCurrentViewPosition;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mTitlebar = getViewById(R.id.titlebar);
        mDataRv = getViewById(R.id.rv_main_data);
    }

    @Override
    protected void setListener() {
        mTitlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate() {
            @Override
            public void onClickRightCtv() {
                forward(DetailActivity.class, REQUEST_CODE_ADD);
            }
        });

        mPlanAdapter = new PlanAdapter(mDataRv);
        mPlanAdapter.setOnItemChildClickListener(this);

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
        mDataRv.setLayoutManager(new LinearLayoutManager(this));
        mDataRv.addItemDecoration(new Divider(this));
        mDataRv.setAdapter(mPlanAdapter);

        loadPlan();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (childView.getId() == R.id.tv_item_plan_delete) {
            mPlanAdapter.closeOpenedSwipeItemLayoutWithAnim();
            mPlanAdapter.removeItem(position);
        } else if (childView.getId() == R.id.rl_item_plan_container) {
            mCurrentViewPosition = position;
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_OPERATE_TYPE, DetailActivity.OPERATE_TYPE_VIEW);
            intent.putExtra(DetailActivity.EXTRA_PLAN, mPlanAdapter.getItem(position));
            forward(intent, REQUEST_CODE_VIEW);
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
                Plan plan = data.getParcelableExtra(DetailActivity.EXTRA_PLAN);
                mPlanAdapter.addFirstItem(plan);
                mDataRv.smoothScrollToPosition(0);
            } else if (requestCode == REQUEST_CODE_VIEW) {
                Plan plan = data.getParcelableExtra(DetailActivity.EXTRA_PLAN);
                mPlanAdapter.setItem(mCurrentViewPosition, plan);
            }
        }
    }

    private void loadPlan() {
        new AsyncTask<Void, Void, List<Plan>>() {
            @Override
            protected void onPreExecute() {
                showLoadingDialog(R.string.loading);
            }

            @Override
            protected List<Plan> doInBackground(Void... params) {
                long beginTime = System.currentTimeMillis();
                List<Plan> plans = PlanDao.queryPlan();
                long time = System.currentTimeMillis() - beginTime;
                if (time < DELAY_TIME) {
                    try {
                        Thread.sleep(DELAY_TIME - time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return plans;
            }

            @Override
            protected void onPostExecute(List<Plan> plans) {
                dismissLoadingDialog();
                mPlanAdapter.setDatas(plans);
            }
        }.execute();
    }

    private final class PlanAdapter extends BGARecyclerViewAdapter<Plan> {
        /**
         * 当前处于打开状态的item
         */
        private List<BGASwipeItemLayout> mOpenedSil = new ArrayList<>();

        public PlanAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_plan);
        }

        @Override
        protected void setItemChildListener(BGAViewHolderHelper viewHolderHelper) {
            BGASwipeItemLayout swipeItemLayout = viewHolderHelper.getView(R.id.sil_item_plan_root);
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
            viewHolderHelper.setItemChildClickListener(R.id.rl_item_plan_container);
            viewHolderHelper.setItemChildClickListener(R.id.tv_item_plan_delete);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, Plan model) {
            helper.setText(R.id.tv_item_plan_content, model.content);
            helper.setText(R.id.tv_item_plan_time, CalendarUtil.formatDisplayTime(model.time));
        }

        public void closeOpenedSwipeItemLayoutWithAnim() {
            for (BGASwipeItemLayout sil : mOpenedSil) {
                sil.closeWithAnim();
            }
            mOpenedSil.clear();
        }
    }

}