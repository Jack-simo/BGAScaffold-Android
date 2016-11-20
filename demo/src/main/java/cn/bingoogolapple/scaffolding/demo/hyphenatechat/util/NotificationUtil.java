package cn.bingoogolapple.scaffolding.demo.hyphenatechat.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.activity.ChatActivity;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.ChatUserModel;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.MessageModel;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.LocalSubscriber;
import cn.bingoogolapple.scaffolding.util.RxUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/20 下午7:18
 * 描述:
 */
public class NotificationUtil {

    private static final int EM_NOTIFICATION_ID = 10;

    private NotificationUtil() {
    }

    public static int getNotificationSmallIcon() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.mipmap.icon_bga_white : R.mipmap.ic_launcher;
    }

    public static void showNewMessageNotification(final MessageModel messageModel) {
        if (AppManager.getInstance().isBackStage()) {
            LiteOrmUtil.getChatUserModelByChatUserName(messageModel.from)
                    .compose(RxUtil.applySchedulers())
                    .subscribe(new LocalSubscriber<ChatUserModel>() {
                        @Override
                        public void onNext(ChatUserModel chatUserModel) {
                            if (chatUserModel == null) {
                                chatUserModel = new ChatUserModel();
                                chatUserModel.nickName = messageModel.from;
                                chatUserModel.chatUserName = messageModel.from;
                                chatUserModel.avatar = "";
                            }
                            showNewMessageNotification(chatUserModel, messageModel);
                        }
                    });
        }
    }

    private static void showNewMessageNotification(ChatUserModel chatUserModel, MessageModel messageModel) {
        if (AppManager.getInstance().isBackStage()) {
            RxUtil.runInUIThread().subscribe(aVoid -> {

                Context context = AppManager.getApp();

                Intent activityIntent = ChatActivity.newIntent(context, chatUserModel);
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                Notification notification = new NotificationCompat.Builder(context)
                        .setSmallIcon(NotificationUtil.getNotificationSmallIcon())
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setTicker("收到新的聊天消息")
                        .setContentTitle(messageModel.nickname)
                        .setContentText(messageModel.msg)
                        .setSound(RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION))
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setWhen(System.currentTimeMillis())
                        .build();

                NotificationManagerCompat.from(context).notify(EM_NOTIFICATION_ID, notification);
            });
        }
    }

}
