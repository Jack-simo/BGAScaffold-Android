package cn.bingoogolapple.scaffolding.demo.hyphenatechat.model;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/13 下午7:38
 * 描述:
 */
public class MessageModel {
    public static final String EXTRA_CHAT_USER_NICKNAME = "nickname";
    public static final String EXTRA_CHAT_USER_AVATAR = "avatar";

    public static final String TYPE_CONTENT_TEXT = "text";
    public static final String TYPE_CONTENT_TIME = "time";

    public static final int SEND_STATUS_INPROGRESS = 0;
    public static final int SEND_STATUS_SUCCESS = 1;
    public static final int SEND_STATUS_ACK = 2;
    public static final int SEND_STATUS_FAIL = 3;

    public int sendStatus;

    public String msgId;

    public long msgTime;

    public String avatar;
    public String nickname;

    public String from;

    public String to;

    public String contentType;

    public String msg;
}
