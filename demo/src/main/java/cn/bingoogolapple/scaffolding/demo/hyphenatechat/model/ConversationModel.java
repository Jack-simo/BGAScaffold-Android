package cn.bingoogolapple.scaffolding.demo.hyphenatechat.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/13 下午7:08
 * 描述:
 */
@Table("conversation_model")
public class ConversationModel extends BaseModel {
    public static final String COL_CONVERSATION_ID = "conversation_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_NICKNAME = "nickname";
    public static final String COL_AVATAR = "avatar";
    public static final String COL_LAST_MSG_CONTENT = "last_msg_content";
    public static final String COL_LAST_MSG_TIME = "last_msg_time";
    public static final String COL_UNREAD_MSG_COUNT = "unread_msg_count";

    @NotNull
    @Unique
    @Column(COL_CONVERSATION_ID)
    public String conversationId;

    @NotNull
    @Unique
    @Column(COL_USERNAME)
    public String username;

    @Unique
    @Column(COL_NICKNAME)
    public String nickname;

    @Default("")
    @NotNull
    @Unique
    @Column(COL_AVATAR)
    public String avatar;

    @Default("")
    @NotNull
    @Column(COL_LAST_MSG_CONTENT)
    public String lastMsgContent;

    @NotNull
    @Column(COL_LAST_MSG_TIME)
    public long lastMsgTime;

    @NotNull
    @Column(COL_UNREAD_MSG_COUNT)
    public int unreadMsgCount;


}