package cn.bingoogolapple.scaffolding.demo.hyphenatechat.util;

import android.util.Pair;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.scaffolding.demo.BuildConfig;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.ConversationModel;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.NetUtil;
import cn.bingoogolapple.scaffolding.util.RxBus;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:06
 * 描述:
 */
public class EmUtil {
    private static final EMConnectionListener sEMConnectionListener = new EMConnectionListener() {
        @Override
        public void onConnected() {
            // 这里是在子线程的
            Logger.i("连接聊天服务器成功");
        }

        @Override
        public void onDisconnected(int error) {
            // 这里是在子线程的
            if (error == EMError.USER_REMOVED) {
                Logger.i("帐号已经被移除");
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                Logger.i("帐号在其他设备登录");
            } else {
                if (NetUtil.isNetworkAvailable()) {
                    Logger.i("连接不到聊天服务器");
                } else {
                    Logger.i("当前网络不可用，请检查网络设置");
                }
            }
        }
    };

    private static final EMConversationListener sEMConversationListener = new EMConversationListener() {

        @Override
        public void onCoversationUpdate() {
            // 这里是在子线程的。如果网络断开期间有新的会话产生，网络重连时也会走该方法
            Logger.i("会话发生了改变");
            RxBus.send(new RxEmEvent.ConversationUpdateEvent());
        }
    };

    private static final EMMessageListener sEMMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // 这里是在子线程的，循环遍历当前收到的消息。如果网络断开期间有新的消息，网络重连时也会走该方法
            Logger.i("onMessageReceived");
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            // 这里是在子线程的，收到透传消息
            Logger.i("onCmdMessageReceived");
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            // 这里是在子线程的，收到已读回执
            Logger.i("onMessageReadAckReceived");
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            // 这里是在子线程的，收到已送达回执
            Logger.i("onMessageDeliveryAckReceived");
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            // 这里是在子线程的，消息状态变动
            Logger.i("onMessageChanged");
        }
    };

    private EmUtil() {
    }

    /**
     * 初始化环信 SDK，在 Application 的 onCreate 方法里调用
     */
    public static void initSdk() {
        EMOptions options = new EMOptions();
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执
        options.setRequireDeliveryAck(true);
        // 设置是否需要服务器收到消息确认
        options.setRequireServerAck(true);
        // 设置是否根据服务器时间排序，默认是 true
        options.setSortMessageByServerTime(false);

        EMClient.getInstance().init(AppManager.getApp(), options);
        EMClient.getInstance().setDebugMode(BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"));

        Logger.i("初始化了环信 SDK");

        EMClient.getInstance().addConnectionListener(sEMConnectionListener);
        EMClient.getInstance().chatManager().addConversationListener(sEMConversationListener);
        EMClient.getInstance().chatManager().addMessageListener(sEMMessageListener);
    }

    public static List<ConversationModel> loadConversationList() {
        Map<String, EMConversation> conversationMap = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<>();

        synchronized (conversationMap) {
            for (EMConversation conversation : conversationMap.values()) {
                if (conversation.getAllMessages().size() > 0) {
                    sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }

        try {
            Collections.sort(sortList, (pair1, pair2) -> {
                if (pair1.first.equals(pair2.first)) {
                    return 0;
                } else if (pair2.first.longValue() > pair1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<ConversationModel> list = new ArrayList<>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(LiteOrmUtil.saveOrUpdateConversation(convertToConversationModel(sortItem.second)));
        }
        return list;
    }

    public static ConversationModel convertToConversationModel(EMConversation emConversation) {
        ConversationModel conversationModel = new ConversationModel();
        conversationModel.conversationId = emConversation.conversationId();
        conversationModel.username = emConversation.getUserName();
        // 昵称
        conversationModel.nickname = conversationModel.username;
        conversationModel.unreadMsgCount = emConversation.getUnreadMsgCount();
        conversationModel.lastMsgTime = emConversation.getLastMessage().getMsgTime();

        if (emConversation.getLastMessage().getType() == EMMessage.Type.TXT) {
            EMTextMessageBody messageBody = (EMTextMessageBody) emConversation.getLastMessage().getBody();
            conversationModel.lastMsgContent = messageBody.getMessage();
            // TODO 卡片类型
        } else {
            conversationModel.lastMsgContent = "";
        }
        // TODO 头像

        return conversationModel;
    }

    public static void markConversationAllMessagesAsRead(String username) {
        EMConversation emConversation = EMClient.getInstance().chatManager().getConversation(username);
        if (emConversation != null) {
            emConversation.markAllMessagesAsRead();
        }
        // TODO 更新数据库
    }

    public static void deleteConversation(String username) {
        EMClient.getInstance().chatManager().deleteConversation(username, true);
        // TODO 更新数据库
    }
}
