package cn.bingoogolapple.alarmclock.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.model.Plan;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.basenote.activity.BaseActivity;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.basenote.widget.Divider;
import cn.bingoogolapple.swipeitemlayout.BGASwipeItemLayout;
import cn.bingoogolapple.titlebar.BGATitlebar;

public class MainActivity extends BaseActivity implements BGAOnItemChildClickListener {
    private RecyclerView mDataRv;
    private PlanAdapter mPlanAdapter;

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
                ToastUtil.show("点击了添加按钮");
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


        testDatas();
    }

    private void testDatas() {
        List<Plan> plans = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            plans.add(new Plan());
        }
        mPlanAdapter.setDatas(plans);
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
            ToastUtil.show("点击了" + position);
        }
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
        }

        public void closeOpenedSwipeItemLayoutWithAnim() {
            for (BGASwipeItemLayout sil : mOpenedSil) {
                sil.closeWithAnim();
            }
            mOpenedSil.clear();
        }
    }

}