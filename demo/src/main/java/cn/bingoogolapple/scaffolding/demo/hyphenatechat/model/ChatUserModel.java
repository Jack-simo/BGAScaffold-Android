package cn.bingoogolapple.scaffolding.demo.hyphenatechat.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/15 下午11:28
 * 描述:http://7xk9dj.com1.z0.glb.clouddn.com/adapter/imgs/1.png
 */
@Table("table_chat_user")
public class ChatUserModel implements Parcelable {
    public static final String COL_CHAT_USER_NAME = "chat_user_name";

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    public long id;
    @Column("username")
    public String nickName;
    @Column("avatar")
    public String avatar;
    @NotNull
    @Unique
    @Column(COL_CHAT_USER_NAME)
    public String chatUserName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.nickName);
        dest.writeString(this.avatar);
        dest.writeString(this.chatUserName);
    }

    public ChatUserModel() {
    }

    protected ChatUserModel(Parcel in) {
        this.id = in.readLong();
        this.nickName = in.readString();
        this.avatar = in.readString();
        this.chatUserName = in.readString();
    }

    public static final Parcelable.Creator<ChatUserModel> CREATOR = new Parcelable.Creator<ChatUserModel>() {
        @Override
        public ChatUserModel createFromParcel(Parcel source) {
            return new ChatUserModel(source);
        }

        @Override
        public ChatUserModel[] newArray(int size) {
            return new ChatUserModel[size];
        }
    };
}