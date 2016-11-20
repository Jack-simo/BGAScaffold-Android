package cn.bingoogolapple.scaffolding.demo.hyphenatechat.util;

import android.content.Intent;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.scaffolding.demo.BuildConfig;
import cn.bingoogolapple.scaffolding.demo.MainActivity;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.ChatUserModel;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.ConversationModel;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.MessageModel;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.LocalSubscriber;
import cn.bingoogolapple.scaffolding.util.NetUtil;
import cn.bingoogolapple.scaffolding.util.RxBus;
import cn.bingoogolapple.scaffolding.util.RxUtil;
import cn.bingoogolapple.scaffolding.util.StringUtil;
import cn.bingoogolapple.scaffolding.util.ToastUtil;
import rx.Observable;
import rx.functions.Func1;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午9:06
 * 描述:
 */
public class EmUtil {
    /**
     * 和上一条消息相差 1 分钟时，增加一条时间类型的消息
     */
    private static final long TIME_MESSAGE_INTERNAL_LIMIT = 60 * 1000;
    /**
     * 登陆或退出失败时，每个五秒重试一次
     */
    private static final int RETRY_DELAY_TIME = 5 * 1000;

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

                if (AppManager.getInstance().isFrontStage()) {
                    ToastUtil.showSafe(errorMsg);

                    Intent intent = new Intent(AppManager.getApp(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    AppManager.getApp().startActivity(intent);
                } else {
                    AppManager.getInstance().exit();
                }
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
                NotificationUtil.showNewMessageNotification(messageModelList.get(messageModelList.size() - 1));
            }

            loadConversationList();
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

            MessageModel messageModel;
            List<MessageModel> messageModelList = new ArrayList<>();
            for (EMMessage message : messages) {
                messageModel = convertToMessageModel(message);
                if (messageModel != null) {
                    messageModelList.add(messageModel);
                }
            }

            if (messageModelList.size() > 0) {
                RxBus.send(new RxEmEvent.MessageReadAckReceivedEvent(messageModelList));
            }
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
        EMClient.getInstance().chatManager().addMessageListener(sEMMessageListener);
    }

    public static void login(String chatUsername, String password) {
        EMClient.getInstance().login(chatUsername, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                Logger.i("登录聊天服务器成功 chatUsername:" + chatUsername);
                EMClient.getInstance().chatManager().loadAllConversations();

                RxBus.send(new RxEmEvent.LoginEvent(true, 0, null));
            }

            @Override
            public void onProgress(int progress, String status) {
                Logger.i("登录聊天服务器进度 progress:" + progress + " status:" + status);
            }

            @Override
            public void onError(int code, String message) {
                Logger.i("登录聊天服务器失败 code:" + code + " message:" + message);
                RxBus.send(new RxEmEvent.LoginEvent(false, code, message));
            }
        });
    }

    public static void logout() {
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                RxBus.send(new RxEmEvent.UnreadMsgCountChangedEvent(0));
            }

            @Override
            public void onProgress(int progress, String status) {
                Logger.i("退出聊天服务器进度 progress:" + progress + " status:" + status);
            }

            @Override
            public void onError(int code, String message) {
                ToastUtil.showSafe("退出聊天服务器失败 code:" + code + " message:" + message);
                EmUtil.logoutRetry();
            }
        });
    }

    /**
     * 延迟 RETRY_DELAY_TIME 秒再次尝试退出环信
     */
    private static void logoutRetry() {
        RxUtil.runInUIThreadDelay(RETRY_DELAY_TIME).subscribe(aVoid -> {
            Logger.i("延迟 " + RETRY_DELAY_TIME + " 秒后再次尝试退出环信");
            EmUtil.logout();
        });
    }

    /**
     * 加载所有会话。收到新消息和需要主动刷新会话或未读消息总数时调用该方法
     *
     * @return
     */
    public static void loadConversationList() {
        LiteOrmUtil.getChatUserModelList().subscribe(new LocalSubscriber<List<ChatUserModel>>() {
            private int mUnreadMsgCount = 0;
            private List<String> mNotExistChatUserNameList = new ArrayList<>();

            @Override
            public void onNext(final List<ChatUserModel> chatUserModelList) {
                // 将环信会话数据模型转换成自己的会话数据模型，并统计本地不存在的聊天用户信息数据集合

                Observable.defer(() -> {
                    try {
                        return Observable.just(EMClient.getInstance().chatManager().getAllConversations());
                    } catch (Exception e) {
                        return Observable.error(e);
                    }
                }).flatMapIterable(new Func1<Map<String, EMConversation>, Iterable<EMConversation>>() {
                    @Override
                    public Iterable<EMConversation> call(Map<String, EMConversation> conversationMap) {
                        return conversationMap.values();
                    }
                }).filter(conversation -> conversation.getLastMessage() != null)
                        .map(conversation -> {
                            mUnreadMsgCount += conversation.getUnreadMsgCount();
                            return convertToConversationModel(conversation, chatUserModelList, mNotExistChatUserNameList);
                        })
                        .toSortedList((conversationModel1, conversationModel2) -> Long.valueOf(conversationModel2.lastMsgTime).compareTo(Long.valueOf(conversationModel1.lastMsgTime)))
                        .compose(RxUtil.applySchedulers())
                        .subscribe(new LocalSubscriber<List<ConversationModel>>() {
                            @Override
                            public void onNext(List<ConversationModel> conversationModelList) {
                                // 发送未读消息数发生改变的事件
                                RxBus.send(new RxEmEvent.UnreadMsgCountChangedEvent(mUnreadMsgCount));
                                // 发送会话列表发生改变的事件
                                RxBus.send(new RxEmEvent.ConversationUpdateEvent(conversationModelList));

                                // TODO 加载本地不存在的聊天用户信息
                            }
                        });
            }
        });
    }

    /**
     * 将环信的会话数据模型转换成自己的数据模型
     *
     * @param conversation             环信的会话数据模型
     * @param chatUserModels           本地存在的聊天用户信息集合
     * @param notExistChatUserNameList 本地不存在的聊天用户名集合
     * @return
     */
    private static ConversationModel convertToConversationModel(EMConversation conversation, List<ChatUserModel> chatUserModels, List<String> notExistChatUserNameList) {
        ConversationModel conversationModel = new ConversationModel();
        conversationModel.conversationId = conversation.conversationId();
        conversationModel.username = conversation.getUserName();
        conversationModel.unreadMsgCount = conversation.getUnreadMsgCount();

        MessageModel messageModel = null;
        if (conversation.getLastMessage() != null) {
            messageModel = convertToMessageModel(conversation.getLastMessage());
        }
        if (messageModel != null) {
            conversationModel.lastMsgTime = messageModel.msgTime;
            conversationModel.lastMsgContent = messageModel.msg;
        } else {
            conversationModel.lastMsgTime = System.currentTimeMillis();
            conversationModel.lastMsgContent = "";
        }

        ChatUserModel chatUserModel = null;
        for (ChatUserModel chatUser : chatUserModels) {
            if (StringUtil.isEqual(conversationModel.username, chatUser.chatUserName)) {
                chatUserModel = chatUser;
                break;
            }
        }
        if (chatUserModel != null) {
            conversationModel.nickname = chatUserModel.nickName;
            conversationModel.avatar = chatUserModel.avatar;
        } else {
            conversationModel.nickname = conversationModel.username;
            conversationModel.avatar = "";

            notExistChatUserNameList.add(conversationModel.username);
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
     * 标记消息集合为已读
     *
     * @param conversation
     * @param messageModelList
     */
    public static void markMessageListAsRead(EMConversation conversation, List<MessageModel> messageModelList) {
        if (messageModelList != null) {
            for (MessageModel messageModel : messageModelList) {
                markMessageAsRead(conversation, messageModel);
            }
        }
    }

    /**
     * 标记消息为已读
     *
     * @param conversation
     * @param messageModel
     */
    public static void markMessageAsRead(EMConversation conversation, MessageModel messageModel) {
        if (StringUtil.isNotEqual(EMClient.getInstance().getCurrentUser(), messageModel.from) && StringUtil.isNotEqual(messageModel.contentType, MessageModel.TYPE_CONTENT_TIME)) {
            conversation.markMessageAsRead(messageModel.msgId);
            try {
                EMClient.getInstance().chatManager().ackMessageRead(messageModel.from, messageModel.msgId);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从内存中加载消息集合
     *
     * @param conversation
     */
    public static List<MessageModel> loadMessageListFromMemory(EMConversation conversation) {
        List<EMMessage> messageList = conversation.getAllMessages();
        return convertToMessageModelList(messageList);
    }

    /**
     * 从本地数据库中加载历史消息集合
     *
     * @param conversation
     * @param startMsgId
     * @param pageSize
     * @return
     */
    public static Observable<List<MessageModel>> loadHistoryMessageListFromDb(EMConversation conversation, String startMsgId, int pageSize) {
        return Observable.defer(() -> Observable.just(conversation.loadMoreMsgFromDB(startMsgId, pageSize)))
                .map(messageList -> convertToMessageModelList(messageList))
                .delaySubscription(150, TimeUnit.MILLISECONDS);
    }

    /**
     * 将环信的消息数据模型集合转换成自己的消息数据模型集合
     *
     * @param messageList 环信的消息数据模型集合
     * @return
     */
    private static List<MessageModel> convertToMessageModelList(List<EMMessage> messageList) {
        List<MessageModel> messageModelList = new ArrayList<>();
        if (messageList != null && messageList.size() > 0) {
            MessageModel messageModel;
            for (EMMessage message : messageList) {
                messageModel = convertToMessageModel(message);
                if (messageModel != null) {
                    messageModelList.add(messageModel);
                }
            }
            refreshTimeMessageModel(messageModelList);
        }

        return messageModelList;
    }

    /**
     * 刷新时间消息列表
     *
     * @param messageModelList
     */
    public static void refreshTimeMessageModel(List<MessageModel> messageModelList) {
        if (messageModelList == null || messageModelList.size() == 0) {
            return;
        }

        // 从底部开始删除
        for (int i = messageModelList.size() - 1; i >= 0; i--) {
            if (messageModelList.get(i).contentType == MessageModel.TYPE_CONTENT_TIME) {
                messageModelList.remove(i);
            }
        }

        MessageModel timeMessageModel;
        // 从底部开始插入
        for (int i = messageModelList.size() - 1; i >= 0; i--) {
            if (i != 0) {
                timeMessageModel = getTimeMessageModel(messageModelList.get(i), messageModelList.get(i - 1));
                if (timeMessageModel != null) {
                    messageModelList.add(i, timeMessageModel);
                }
            }
        }
    }

    /**
     * 获取时间消息模型。如果不需要添加时间消息模型就返回null
     *
     * @param currentMessageModel 上一个消息模型
     * @param lastMessageModel    当前消息模型
     * @return
     */
    public static MessageModel getTimeMessageModel(MessageModel currentMessageModel, MessageModel lastMessageModel) {
        long currentMsgTime = currentMessageModel.msgTime;
        long previousMsgTime = lastMessageModel.msgTime;
        long difTime = currentMsgTime - previousMsgTime;
        if (difTime > TIME_MESSAGE_INTERNAL_LIMIT && currentMessageModel.contentType != MessageModel.TYPE_CONTENT_TIME) {
            MessageModel timeMessageModel = new MessageModel();
            timeMessageModel.contentType = MessageModel.TYPE_CONTENT_TIME;
            timeMessageModel.msgTime = currentMsgTime;
            return timeMessageModel;
        }
        return null;
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
            messageModel.to = message.getTo();

            if (message.isAcked()) {
                messageModel.sendStatus = MessageModel.SEND_STATUS_ACK;
            } else if (message.status() == EMMessage.Status.SUCCESS) {
                messageModel.sendStatus = MessageModel.SEND_STATUS_SUCCESS;
            } else if (message.status() == EMMessage.Status.FAIL) {
                messageModel.sendStatus = MessageModel.SEND_STATUS_FAIL;
            } else {
                messageModel.sendStatus = MessageModel.SEND_STATUS_INPROGRESS;
            }

            messageModel.nickname = message.getStringAttribute(MessageModel.EXTRA_CHAT_USER_NICKNAME, message.getFrom());
            messageModel.avatar = message.getStringAttribute(MessageModel.EXTRA_CHAT_USER_AVATAR, "");

            // TODO 处理自定义消息类型
            messageModel.contentType = MessageModel.TYPE_CONTENT_TEXT;

            return messageModel;
        }
        return null;
    }

    /**
     * 发送环信消息实体
     *
     * @param emMessage 环信消息
     * @return 自己的消息实体
     */
    public static MessageModel sendMessage(final EMMessage emMessage) {
        emMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                RxBus.send(new RxEmEvent.MessageSendSuccessEvent(convertToMessageModel(emMessage)));
            }

            @Override
            public void onError(int code, String message) {
                Logger.e("消息发送失败 code:" + code + " message:" + message);
                RxBus.send(new RxEmEvent.MessageSendFailureEvent(convertToMessageModel(emMessage)));
            }

            @Override
            public void onProgress(int progress, String status) {
                // 消息发送进度，一般只有在发送图片和文件等消息才会有回调，txt 不回调
                Logger.i("消息发送中 progress:" + progress + " status:" + status);
            }
        });
        EMClient.getInstance().chatManager().sendMessage(emMessage);
        return EmUtil.convertToMessageModel(emMessage);
    }

    public static void resendMessage(String msgId) {
        EMMessage emMessage = EMClient.getInstance().chatManager().getMessage(msgId);
        emMessage.setStatus(EMMessage.Status.CREATE);
        EmUtil.sendMessage(emMessage);
    }

    /**
     * 创建环信文本消息实体
     *
     * @param msg            消息内容
     * @param toChatUserName 消息接收者的环信用户名
     * @return 环信消息实体
     */
    public static EMMessage createTextMessage(String msg, String toChatUserName) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(msg, toChatUserName);
        emMessage.setAttribute(MessageModel.EXTRA_CHAT_USER_NICKNAME, "");
        emMessage.setAttribute(MessageModel.EXTRA_CHAT_USER_AVATAR, "");
        return emMessage;
    }
}
