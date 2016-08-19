package cn.bingoogolapple.alarmclock.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import cn.bingoogolapple.alarmclock.data.Plan;
import cn.bingoogolapple.alarmclock.receiver.AlarmReceiver;
import cn.bingoogolapple.alarmclock.editplan.EditPlanActivity;
import cn.bingoogolapple.basenote.App;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 下午9:16
 * 描述:
 */
public class AlarmUtil {
    private static AlarmManager sAlarmManager;

    private AlarmUtil() {
    }

    private static AlarmManager getAlarmManager() {
        if (sAlarmManager == null) {
            synchronized (AlarmUtil.class) {
                if (sAlarmManager == null) {
                    sAlarmManager = (AlarmManager) App.getInstance().getSystemService(Context.ALARM_SERVICE);
                }
            }
        }
        return sAlarmManager;
    }

    public static void addAlarm(Plan plan) {
        Intent intent = new Intent(App.getInstance(), AlarmReceiver.class);
        intent.putExtra(EditPlanActivity.EXTRA_PLAN, plan);
        getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, plan.time, 5 * 60 * 1000, PendingIntent.getBroadcast(App.getInstance(), (int) plan.id, intent, 0));
    }

    public static void cancelAlarm(Plan plan) {
        getAlarmManager().cancel(PendingIntent.getBroadcast(App.getInstance(), (int) plan.id, new Intent(App.getInstance(), AlarmReceiver.class), 0));
    }
}