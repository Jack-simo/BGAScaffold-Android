package cn.bingoogolapple.scaffolding.demo.hyphenatechat.util;

import java.util.List;

import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.ConversationModel;
import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.MessageModel;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/7 下午2:17
 * 描述:RxBus 事件
 */
public class RxEmEvent {

    public static class LoginEvent {
        public boolean mIsSuccess;
        public int mCode;
        public String mMessage;

        public LoginEvent(boolean isSuccess, int code, String message) {
            mIsSuccess = isSuccess;
            mCode = code;
            mMessage = message;
        }
    }

    /**
     * 会话发生了改变事件
     */
    public static class ConversationUpdateEvent {
        public List<ConversationModel> mConversationModelList;

        public ConversationUpdateEvent(List<ConversationModel> conversationModelList) {
            mConversationModelList = conversationModelList;
        }
    }

    /**
     * 消息发送成功事件
     */
    public static class MessageSendSuccessEvent {
        public MessageModel mMessageModel;

        public MessageSendSuccessEvent(MessageModel messageModel) {
            mMessageModel = messageModel;
        }
    }

    /**
     * 消息发送失败事件
     */
    public static class MessageSendFailureEvent {
        public MessageModel mMessageModel;

        public MessageSendFailureEvent(MessageModel messageModel) {
            mMessageModel = messageModel;
        }
    }

    /**
     * 收到新消息事件
     */
    public static class MessageReceivedEvent {
        public List<MessageModel> mMessageModelList;

        public MessageReceivedEvent(List<MessageModel> messageModelList) {
            mMessageModelList = messageModelList;
        }
    }

    /**
     * 连接上环信服务器事件
     */
    public static class EMConnectedEvent {
    }

    /**
     * 和环信服务器断开连接事件
     */
    public static class EMDisconnectedEvent {
        public int mErrorCode;
        public String mErrorMsg;

        public EMDisconnectedEvent(int errorCode, String errorMsg) {
            mErrorCode = errorCode;
            mErrorMsg = errorMsg;
        }
    }

    /**
     * 未读消息数量发生变化
     */
    public static class UnreadMsgCountChangedEvent {
        public int mUnreadMsgCount;

        public UnreadMsgCountChangedEvent(int unreadMsgCount) {
            mUnreadMsgCount = unreadMsgCount;
        }
    }

}
