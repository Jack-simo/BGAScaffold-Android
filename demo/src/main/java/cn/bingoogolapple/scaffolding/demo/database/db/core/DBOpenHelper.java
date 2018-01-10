package cn.bingoogolapple.scaffolding.demo.database.db.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.orhanobut.logger.Logger;

import cn.bingoogolapple.scaffolding.demo.database.db.table.GoodsTable;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2018/1/9
 * 描述:
 */
class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "test.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase mDatabase;

    DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDatabase = db;
        createFtsGoodsTable();
        createGoodsTable();
    }

    private void createFtsGoodsTable() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE VIRTUAL TABLE " + GoodsTable.TABLE_NAME_GOODS_FTS + " USING fts3 ( ");
        sql.append(GoodsTable.CODE + ", ");
        sql.append(GoodsTable.BARCODE + ", ");
        sql.append(GoodsTable.NAME + ", ");
        sql.append(GoodsTable.UNIT + ", ");
        sql.append(GoodsTable.ADDRESS + ", ");
        sql.append(GoodsTable.BRAND + ", ");
        sql.append("tokenize=icu zh_CN ");
        sql.append(");");
        mDatabase.execSQL(sql.toString());
    }

    private void createGoodsTable() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE " + GoodsTable.TABLE_NAME_GOODS + " ( ");
        sql.append(GoodsTable.CODE + ", ");
        sql.append(GoodsTable.BARCODE + ", ");
        sql.append(GoodsTable.NAME + ", ");
        sql.append(GoodsTable.UNIT + ", ");
        sql.append(GoodsTable.ADDRESS + ", ");
        sql.append(GoodsTable.BRAND + " ");
        sql.append(");");
        mDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.w("Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + GoodsTable.TABLE_NAME_GOODS_FTS);
        db.execSQL("DROP TABLE IF EXISTS " + GoodsTable.TABLE_NAME_GOODS);
        onCreate(db);
    }
}