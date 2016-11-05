package cn.bingoogolapple.alarmclock.plans;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午5:28
 * 描述:
 */

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.data.Plan;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.scaffolding.util.CalendarUtil;
import cn.bingoogolapple.swipeitemlayout.BGASwipeItemLayout;

public class PlanAdapter extends BGARecyclerViewAdapter<Plan> {
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