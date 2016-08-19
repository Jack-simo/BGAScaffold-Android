package cn.bingoogolapple.alarmclock.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import cn.bingoogolapple.alarmclock.data.dao.DBOpenHelper;
import cn.bingoogolapple.basenote.util.Logger;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/10/11 下午4:18
 * 描述:
 */
public class PlanProvider extends ContentProvider {
    private static final String TAG = PlanProvider.class.getSimpleName();
    public static final String AUTHORITIES = PlanProvider.class.getCanonicalName();
    // 给内容观察者用的
    public static final String PATH_PREFIX = "content://" + AUTHORITIES;
    public static final String PATH_PLAN = "/plan";
    public static final Uri URI_PLAN = Uri.parse(PATH_PREFIX + PATH_PLAN);

    public static final int MATCHED_CODE_PLAN = 1;
    private static UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(AUTHORITIES, PATH_PLAN, MATCHED_CODE_PLAN);
    }

    private DBOpenHelper mDBOpenHelper;

    @Override
    public boolean onCreate() {
        mDBOpenHelper = new DBOpenHelper(getContext());
        return mDBOpenHelper != null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int code = sUriMatcher.match(uri);
        switch (code) {
            case MATCHED_CODE_PLAN:
                SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
                long newlyId = db.insert(mDBOpenHelper.T_PLAN, "", values);
                if (newlyId != -1) {
                    Logger.i(TAG, "添加计划成功");
                    // 拼接最新的Uri
                    uri = ContentUris.withAppendedId(uri, newlyId);

                    // 通知内容观察者数据有改变，null表示所有的内容观察者都能收到
                    getContext().getContentResolver().notifyChange(URI_PLAN, null);
                }
                break;
            default:
                uri = ContentUris.withAppendedId(uri, -1);
                break;
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int affectedCount = 0;
        int matchedCode = sUriMatcher.match(uri);
        switch (matchedCode) {
            case MATCHED_CODE_PLAN:
                SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
                affectedCount = db.delete(mDBOpenHelper.T_PLAN, selection, selectionArgs);
                if (affectedCount > 0) {
                    getContext().getContentResolver().notifyChange(URI_PLAN, null);
                }
                break;
            default:
                break;
        }
        return affectedCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int affectedCount = 0;
        int code = sUriMatcher.match(uri);
        switch (code) {
            case MATCHED_CODE_PLAN:
                SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
                affectedCount = db.update(mDBOpenHelper.T_PLAN, values, selection, selectionArgs);
                if (affectedCount > 0) {
                    getContext().getContentResolver().notifyChange(URI_PLAN, null);
                }
                break;
            default:
                break;
        }
        return affectedCount;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        int code = sUriMatcher.match(uri);
        switch (code) {
            case MATCHED_CODE_PLAN:
                SQLiteDatabase db = mDBOpenHelper.getReadableDatabase();
                cursor = db.query(mDBOpenHelper.T_PLAN, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }
}