package cn.bingoogolapple.scaffolding.demo.hyphenatechat.model;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/13 下午7:38
 * 描述:
 */
public class MessageModel {
    public static final String TYPE_CONTENT_TEXT = "text";
    public static final String TYPE_CONTENT_CARD = "card";

    public String msgId;

    public long msgTime;

    public String avatar;

    public boolean isSendByMe;

    public String contentType;

    public String msg;

}
