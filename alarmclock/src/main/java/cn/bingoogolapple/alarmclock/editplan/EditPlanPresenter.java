package cn.bingoogolapple.alarmclock.editplan;

import cn.bingoogolapple.alarmclock.data.Plan;
import cn.bingoogolapple.basenote.presenter.BasePresenter;
import cn.bingoogolapple.basenote.view.BaseView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/18 下午10:06
 * 描述:
 */
public interface EditPlanPresenter extends BasePresenter {

    interface View extends BaseView {
        void addOrUpdateSuccess();

        void deleteSuccess();
    }

    void addPlan(Plan plan);

    void updatePlan(Plan plan, long time, String content);

    void deletePlan(Plan plan);
}