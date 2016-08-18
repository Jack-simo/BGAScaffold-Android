package cn.bingoogolapple.alarmclock.presenter.impl;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.dao.PlanDao;
import cn.bingoogolapple.alarmclock.model.Plan;
import cn.bingoogolapple.alarmclock.presenter.EditPlanPresenter;
import cn.bingoogolapple.alarmclock.util.AlarmUtil;
import cn.bingoogolapple.basenote.presenter.BasePresenterImpl;
import cn.bingoogolapple.basenote.util.LocalSubscriber;
import cn.bingoogolapple.basenote.util.RxUtil;
import rx.Observable;
import rx.functions.Func0;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/18 下午11:18
 * 描述:
 */
public class EditPlanPresenterImpl extends BasePresenterImpl<EditPlanPresenter.View> implements EditPlanPresenter {

    public EditPlanPresenterImpl(View view) {
        super(view);
    }

    @Override
    public void addPlan(Plan plan) {
        mCompositeSubscription.add(Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                try {
                    return Observable.just(PlanDao.insertPlan(plan));
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).compose(RxUtil.applySchedulers())
                .subscribe(new LocalSubscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean result) {
                        if (result) {
                            AlarmUtil.addAlarm(plan);
                            mView.addOrUpdateSuccess();
                        } else {
                            mView.showMsg(R.string.toast_add_plan_failure);
                        }
                    }
                }));
    }

    @Override
    public void updatePlan(Plan plan, long time, String content) {
        mCompositeSubscription.add(Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                try {
                    return Observable.just(PlanDao.updatePlan(plan.id, time, content, Plan.STATUS_NOT_HANDLE));
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).compose(RxUtil.applySchedulers())
                .subscribe(new LocalSubscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean result) {
                        if (result) {
                            AlarmUtil.cancelAlarm(plan);
                            plan.time = time;
                            plan.content = content;
                            plan.status = Plan.STATUS_NOT_HANDLE;
                            AlarmUtil.addAlarm(plan);
                            mView.addOrUpdateSuccess();
                        } else {
                            mView.showMsg(R.string.toast_update_plan_failure);
                        }
                    }
                }));
    }

    @Override
    public void deletePlan(Plan plan) {
        mCompositeSubscription.add(Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                try {
                    return Observable.just(PlanDao.deletePlan(plan.id));
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).compose(RxUtil.applySchedulers())
                .subscribe(new LocalSubscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean result) {
                        if (result) {
                            AlarmUtil.cancelAlarm(plan);
                            mView.deleteSuccess();
                        } else {
                            mView.showMsg(R.string.toast_delete_plan_failure);
                        }
                    }
                }));
    }
}
