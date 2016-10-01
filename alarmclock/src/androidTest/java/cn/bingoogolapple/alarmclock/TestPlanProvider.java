package cn.bingoogolapple.alarmclock;

import android.test.AndroidTestCase;

import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.List;

import cn.bingoogolapple.alarmclock.dao.PlanDao;
import cn.bingoogolapple.alarmclock.model.Plan;
import cn.bingoogolapple.basenote.util.CalendarUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 下午4:36
 * 描述:
 */
public class TestPlanProvider extends AndroidTestCase {
    private static final String TAG = TestPlanProvider.class.getSimpleName();

    public void testInsertPlan() {
        Plan plan = new Plan();
        plan.time = CalendarUtil.getZeroSecondCalendar().getTimeInMillis();
        plan.content = "我是新加的内容";
        plan.status = 0;
        if (PlanDao.insertPlan(plan)) {
            Logger.i("添加计划成功:" + plan.toString());
        } else {
            Logger.i("添加计划失败");
        }
    }

    public void testDeletePlan() {
        if (PlanDao.deletePlan(1)) {
            Logger.i("删除计划成功");
        } else {
            Logger.i("删除计划失败");
        }
    }

    public void testUpdatePlan() {
        Calendar calendar = CalendarUtil.getZeroSecondCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        if (PlanDao.updatePlan(1, calendar.getTimeInMillis(), "我是修改后的内容", 1)) {
            Logger.i("修改计划成功");
        } else {
            Logger.i("修改计划失败");
        }
    }

    public void testQueryPlan() {
        List<Plan> plans = PlanDao.queryPlan();
        for (Plan plan : plans) {
            Logger.i(plan.toString());
        }
    }
}