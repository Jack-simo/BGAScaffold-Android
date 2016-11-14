package cn.bingoogolapple.scaffolding.demo.hyphenatechat.util;

import android.util.Pair;

import com.hyphenate.EMCallBack;
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
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.MessageModel;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.NetUtil;
import cn.bingoogolapple.scaffolding.util.RxBus;
import cn.bingoogolapple.scaffolding.util.StringUtil;

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
            RxBus.send(new RxEmEvent.EMConnectedEvent());
        }

        @Override
        public void onDisconnected(int error) {
            // 这里是在子线程的
            String errorMsg = "";
            if (error == EMError.USER_REMOVED) {
                errorMsg = "帐号已经被移除";
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                errorMsg = "帐号在其他设备登录";
            } else {
                if (NetUtil.isNetworkAvailable()) {
                    errorMsg = "连接不到聊天服务器";
                } else {
                    errorMsg = "当前网络不可用，请检查网络设置";
                }
            }
            Logger.i(errorMsg);
            RxBus.send(new RxEmEvent.EMDisconnectedEvent(error, errorMsg));
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
            MessageModel messageModel = null;
            List<MessageModel> messageModelList = new ArrayList<>();
            for (EMMessage message : messages) {
                messageModel = convertToMessageModel(message);
                if (messageModel != null) {
                    messageModelList.add(messageModel);
                }
            }

            if (messageModelList.size() > 0) {
                RxBus.send(new RxEmEvent.MessageReceivedEvent(messageModelList));
            }
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

    /**
     * 加载所有会话
     *
     * @return
     */
    public static List<ConversationModel> loadConversationList() {
        Map<String, EMConversation> conversationMap = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> conversationList = new ArrayList<>();

        synchronized (conversationMap) {
            for (EMConversation conversation : conversationMap.values()) {
                if (conversation.getAllMessages().size() > 0) {
                    conversationList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }

        try {
            Collections.sort(conversationList, (pair1, pair2) -> {
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

        List<ConversationModel> conversationModelList = new ArrayList<>();
        for (Pair<Long, EMConversation> sortItem : conversationList) {
            conversationModelList.add(convertToConversationModel(sortItem.second));
        }
        return conversationModelList;
    }

    /**
     * 将环信的会话数据模型转换成自己的数据模型
     *
     * @param conversation 环信的会话数据模型
     * @return
     */
    public static ConversationModel convertToConversationModel(EMConversation conversation) {
        ConversationModel conversationModel = new ConversationModel();
        conversationModel.conversationId = conversation.conversationId();
        conversationModel.username = conversation.getUserName();
        // TODO 处理昵称
        conversationModel.nickname = conversationModel.username;
        conversationModel.unreadMsgCount = conversation.getUnreadMsgCount();

        MessageModel messageModel = null;
        if (conversation.getLastMessage() != null) {
            messageModel = convertToMessageModel(conversation.getLastMessage());
        }
        if (messageModel != null) {
            // TODO 处理头像
            conversationModel.avatar = messageModel.avatar;
            conversationModel.lastMsgTime = messageModel.msgTime;
            conversationModel.lastMsgContent = messageModel.msg;
        } else {
            // TODO 处理头像
            conversationModel.avatar = "https://avatars2.githubusercontent.com/u/11001615?v=3&s=460";
            conversationModel.lastMsgTime = System.currentTimeMillis();
        }

        return conversationModel;
    }

    /**
     * 将和指定用户会话的所有消息标记为已读
     *
     * @param username 环信用户名
     */
    public static void markConversationAllMessagesAsRead(String username) {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        if (conversation != null) {
            conversation.markAllMessagesAsRead();
        }
    }

    /**
     * 删除和指定用户会话
     *
     * @param username
     */
    public static void deleteConversation(String username) {
        EMClient.getInstance().chatManager().deleteConversation(username, true);
    }

    /**
     * 加载指定会话下的所有消息
     *
     * @param conversation
     */
    public static List<MessageModel> loadMessageList(EMConversation conversation) {
        List<EMMessage> messageList = conversation.getAllMessages();
        List<MessageModel> messageModelList = new ArrayList<>();
        if (messageList != null) {
            MessageModel messageModel;
            for (EMMessage message : messageList) {
                messageModel = convertToMessageModel(message);
                if (messageModel != null) {
                    messageModelList.add(messageModel);
                }
            }
        }
        return messageModelList;
    }

    /**
     * 将环信的消息数据模型转换成自己的消息数据模型
     *
     * @param message 环信的消息数据模型
     * @return
     */
    public static MessageModel convertToMessageModel(EMMessage message) {
        if (message.getType() == EMMessage.Type.TXT) {
            MessageModel messageModel = new MessageModel();
            EMTextMessageBody messageBody = (EMTextMessageBody) message.getBody();
            messageModel.msgId = message.getMsgId();
            messageModel.msg = messageBody.getMessage();
            messageModel.msgTime = message.getMsgTime();
            messageModel.from = message.getFrom();

            // TODO 处理头像
            if (StringUtil.isEqual(EMClient.getInstance().getCurrentUser(), messageModel.from)) {
                messageModel.avatar = "https://avatars2.githubusercontent.com/u/8949716?v=3&s=460";
            } else {
                messageModel.avatar = "https://avatars2.githubusercontent.com/u/11001615?v=3&s=460";
            }

            // TODO 处理自定义消息类型
            messageModel.contentType = MessageModel.TYPE_CONTENT_TEXT;
            return messageModel;
        }
        return null;
    }

    public static void sendMessage(final EMMessage emMessage) {
        emMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                RxBus.send(new RxEmEvent.MessageSendSuccessEvent(convertToMessageModel(emMessage)));
            }

            @Override
            public void onError(int code, String message) {
                Logger.e("消息发送失败 code:" + code + " message:" + message);
                RxBus.send(new RxEmEvent.MessageSendSuccessEvent(convertToMessageModel(emMessage)));
            }

            @Override
            public void onProgress(int progress, String status) {
                // 消息发送进度，一般只有在发送图片和文件等消息才会有回调，txt 不回调
                Logger.i("消息发送中 progress:" + progress + " status:" + status);
            }
        });
        EMClient.getInstance().chatManager().sendMessage(emMessage);
    }
}
