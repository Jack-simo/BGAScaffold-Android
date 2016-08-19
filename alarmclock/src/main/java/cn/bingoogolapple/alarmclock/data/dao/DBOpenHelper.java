package cn.bingoogolapple.alarmclock.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 下午4:19
 * 描述:
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    public static final String T_PLAN = "t_plan";

    public DBOpenHelper(Context context) {
        super(context, "bgaalarmclock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPlanTable(db);
    }

    private void createPlanTable(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE " + T_PLAN + " ( ");
        sql.append(PlanTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sql.append(PlanTable.TIME + " INTEGER, ");
        sql.append(PlanTable.CONTENT + " TEXT, ");
        sql.append(PlanTable.STATUS + " INTEGER ");
        sql.append(");");
        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public interface PlanTable extends BaseColumns {
        // BaseColumns接口默认添加了列_id
        String TIME = "TIME";
        String CONTENT = "CONTENT";
        String STATUS = "STATUS";
    }
}