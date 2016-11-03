package cn.bingoogolapple.alarmclock.plans;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.data.Plan;
import cn.bingoogolapple.alarmclock.data.dao.PlanDao;
import cn.bingoogolapple.alarmclock.util.AlarmUtil;
import cn.bingoogolapple.basenote.presenter.BasePresenterImpl;
import cn.bingoogolapple.basenote.util.CalendarUtil;
import cn.bingoogolapple.basenote.util.LocalSubscriber;
import cn.bingoogolapple.basenote.util.RxUtil;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/18 下午9:47
 * 描述:
 */
public class PlansPresenterImpl extends BasePresenterImpl<PlansPresenter.View> implements PlansPresenter {

    public PlansPresenterImpl(View view) {
        super(view);
    }

    @Override
    public void loadPlans() {
        mCompositeSubscription.add(Observable.create(new Observable.OnSubscribe<List<Plan>>() {
            @Override
            public void call(Subscriber<? super List<Plan>> subscriber) {
                try {
                    long beginTime = System.currentTimeMillis();
                    List<Plan> plans = PlanDao.queryPlan();
                    long endTime = System.currentTimeMillis();
                    long time = endTime - beginTime;
                    if (time < 1000) {
                        try {
                            Thread.sleep(1000 - time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!subscriber.isUnsubscribed()) {
                        if (plans != null) {
                            subscriber.onNext(plans);
                        }
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        }).compose(RxUtil.applySchedulers())
                .subscribe(new LocalSubscriber<List<Plan>>(mView.getBaseActivity()) {
                    @Override
                    public void onNext(List<Plan> plans) {
                        mView.showPlans(plans);
                    }
                }));
    }

    @Override
    public void updatePlanStatus(int position, Plan plan) {
        final int newStatus = plan.status == Plan.STATUS_ALREADY_HANDLE ? Plan.STATUS_NOT_HANDLE : Plan.STATUS_ALREADY_HANDLE;

        mCompositeSubscription.add(Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                try {
                    return Observable.just(PlanDao.updatePlan(plan.id, plan.time, plan.content, newStatus));
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).compose(RxUtil.applySchedulers())
                .subscribe(new LocalSubscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean result) {
                        if (result) {
                            plan.status = newStatus;
                            if (newStatus == Plan.STATUS_NOT_HANDLE && plan.time > CalendarUtil.getCalendar().getTimeInMillis()) {
                                AlarmUtil.addAlarm(plan);
                            } else {
                                AlarmUtil.cancelAlarm(plan);
                            }
                        } else {
                            mView.showMsg(R.string.toast_update_plan_failure);
                        }

                        // 不管修改成功还是修改失败都要更新下item来保证开关的状态
                        mView.notifyItemChanged(position);
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
                            mView.removeCurrentSelectedItem();
                        } else {
                            mView.showMsg(R.string.toast_delete_plan_failure);
                        }
                    }
                }));
    }
}