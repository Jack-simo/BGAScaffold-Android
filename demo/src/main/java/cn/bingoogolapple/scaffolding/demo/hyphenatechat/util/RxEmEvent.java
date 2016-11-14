package cn.bingoogolapple.scaffolding.demo.hyphenatechat.util;

import java.util.List;

import cn.bingoogolapple.scaffolding.demo.hyphenatechat.model.MessageModel;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/7 下午2:17
 * 描述:RxBus 事件
 */
public class RxEmEvent {
    /**
     * 会话发生了改变
     */
    public static class ConversationUpdateEvent {
    }

    public static class MessageSendSuccessEvent {
        public MessageModel mMessageModel;

        public MessageSendSuccessEvent(MessageModel messageModel) {
            mMessageModel = messageModel;
        }
    }

    public static class MessageSendFailureEvent {
        public MessageModel mMessageModel;

        public MessageSendFailureEvent(MessageModel messageModel) {
            mMessageModel = messageModel;
        }
    }

    public static class MessageReceivedEvent {
        public List<MessageModel> mMessageModelList;

        public MessageReceivedEvent(List<MessageModel> messageModelList) {
            mMessageModelList = messageModelList;
        }
    }

    public static class EMConnectedEvent {
    }

    public static class EMDisconnectedEvent {
        public int mErrorCode;
        public String mErrorMsg;

        public EMDisconnectedEvent(int errorCode, String errorMsg) {
            mErrorCode = errorCode;
            mErrorMsg = errorMsg;
        }
    }

}
