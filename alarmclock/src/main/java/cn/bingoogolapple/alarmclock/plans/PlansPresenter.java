package cn.bingoogolapple.alarmclock.plans;

import java.util.List;

import cn.bingoogolapple.alarmclock.data.Plan;
import cn.bingoogolapple.basenote.presenter.BasePresenter;
import cn.bingoogolapple.basenote.view.BaseView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/18 下午9:37
 * 描述:
 */
public interface PlansPresenter extends BasePresenter {

    interface View extends BaseView {
        void showPlans(List<Plan> plans);

        void notifyItemChanged(int position);

        void removeCurrentSelectedItem();
    }

    void loadPlans();

    void updatePlanStatus(int position, Plan plan);

    void deletePlan(Plan plan);
}