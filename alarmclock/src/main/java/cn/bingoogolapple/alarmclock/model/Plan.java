package cn.bingoogolapple.alarmclock.model;

import android.os.Parcel;
import android.os.Parcelable;

import cn.bingoogolapple.basenote.util.CalendarUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/29 上午1:33
 * 描述:
 */
public class Plan implements Parcelable {
    public static final int STATUS_NOT_HANDLE = 0;
    public static final int STATUS_ALREADY_HANDLE = 1;
    public long id;
    public long time;
    public String content;
    public int status;

    @Override
    public String toString() {
        return "Plan{" +
                "id=" + id +
                ", time=" + CalendarUtil.formatDisplayTime(time) +
                ", content='" + content + '\'' +
                ", status=" + (status == STATUS_NOT_HANDLE ? "未处理" : "已处理") +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.time);
        dest.writeString(this.content);
        dest.writeInt(this.status);
    }

    public Plan() {
    }

    protected Plan(Parcel in) {
        this.id = in.readLong();
        this.time = in.readLong();
        this.content = in.readString();
        this.status = in.readInt();
    }

    public static final Parcelable.Creator<Plan> CREATOR = new Parcelable.Creator<Plan>() {
        public Plan createFromParcel(Parcel source) {
            return new Plan(source);
        }

        public Plan[] newArray(int size) {
            return new Plan[size];
        }
    };
}