package cn.bingoogolapple.scaffolding.demo.hyphenatechat.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/13 下午7:38
 * 描述:
 */
@Table("message_model")
public class MessageModel extends BaseModel {
    private static final String COL_MSG_ID = "msg_id";
    private static final String COL_MSG_TIME = "msg_time";
    private static final String COL_AVATAR = "avatar";
    private static final String COL_TYPE_SENDER = "sender_type";
    private static final String COL_TYPE_CONTENT = "content_type";

    public static final String TYPE_SENDER_ME = "me";
    public static final String TYPE_SENDER_OTHER = "other";

    public static final String TYPE_CONTENT_TEXT = "text";
    public static final String TYPE_CONTENT_CARD = "card";

    @Unique
    @Column(COL_MSG_ID)
    public String msgId;

    @NotNull
    @Column(COL_MSG_TIME)
    public String msgTime;

    @NotNull
    @Unique
    @Column(COL_AVATAR)
    public String avatar;

    @NotNull
    @Unique
    @Column(COL_TYPE_SENDER)
    public String senderType;

    @NotNull
    @Unique
    @Column(COL_TYPE_CONTENT)
    public String contentType;

}
