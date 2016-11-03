package cn.bingoogolapple.alarmclock.data.dao;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.alarmclock.data.Plan;
import cn.bingoogolapple.alarmclock.provider.PlanProvider;
import cn.bingoogolapple.basenote.App;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 下午4:16
 * 描述:
 */
public class PlanDao {

    public static boolean insertPlan(Plan plan) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.PlanTable.TIME, plan.time);
        values.put(DBOpenHelper.PlanTable.CONTENT, plan.content);
        values.put(DBOpenHelper.PlanTable.STATUS, plan.status);

        Uri uri = App.getInstance().getContentResolver().insert(PlanProvider.URI_PLAN, values);
        long newlyId = ContentUris.parseId(uri);
        if (newlyId != -1) {
            plan.id = newlyId;
            Logger.i("添加成功" + newlyId);
            return true;
        } else {
            return false;
        }
    }

    public static boolean deletePlan(long id) {
        int deletedCount = App.getInstance().getContentResolver().delete(PlanProvider.URI_PLAN, DBOpenHelper.PlanTable._ID + "=?", new String[]{"" + id});
        if (deletedCount > 0) {
            Logger.i("删除成功 " + deletedCount);
            return true;
        } else {
            return false;
        }
    }

    public static boolean updatePlan(long id, long time, String content, int status) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.PlanTable.TIME, time);
        values.put(DBOpenHelper.PlanTable.CONTENT, content);
        values.put(DBOpenHelper.PlanTable.STATUS, status);
        int updatedCount = App.getInstance().getContentResolver().update(PlanProvider.URI_PLAN, values, DBOpenHelper.PlanTable._ID + "=?", new String[]{String.valueOf(id)});
        if (updatedCount > 0) {
            Logger.i("修改成功" + updatedCount);
            return true;
        } else {
            return false;
        }
    }

    public static List<Plan> queryPlan() {
        List<Plan> plans = new ArrayList<>();
        Cursor cursor = App.getInstance().getContentResolver().query(PlanProvider.URI_PLAN, null, null, null, DBOpenHelper.PlanTable.TIME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Plan plan = new Plan();
                plan.id = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.PlanTable._ID));
                plan.time = cursor.getLong(cursor.getColumnIndex(DBOpenHelper.PlanTable.TIME));
                plan.content = cursor.getString(cursor.getColumnIndex(DBOpenHelper.PlanTable.CONTENT));
                plan.status = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.PlanTable.STATUS));
                plans.add(plan);
            }
            cursor.close();
        }
        return plans;
    }
}