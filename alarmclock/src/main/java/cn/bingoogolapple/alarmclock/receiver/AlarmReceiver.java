package cn.bingoogolapple.alarmclock.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v7.app.NotificationCompat;

import cn.bingoogolapple.alarmclock.R;
import cn.bingoogolapple.alarmclock.data.Plan;
import cn.bingoogolapple.alarmclock.alarm.AlarmActivity;
import cn.bingoogolapple.alarmclock.editplan.EditPlanActivity;
import cn.bingoogolapple.alarmclock.util.AlarmUtil;
import cn.bingoogolapple.scaffolding.App;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 下午9:13
 * 描述:
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Plan plan = intent.getParcelableExtra(EditPlanActivity.EXTRA_PLAN);
        if (plan == null) {
            return;
        }

        AlarmUtil.cancelAlarm(plan);

        Intent activityIntent = AlarmActivity.newIntent(context, plan);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setTicker(plan.content);
        builder.setContentText(plan.content);
        builder.setSound(RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION));
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setWhen(System.currentTimeMillis());
        App.getInstance().addNotification((int) plan.id, builder.build());
    }
}