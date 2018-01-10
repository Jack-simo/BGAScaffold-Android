package cn.bingoogolapple.scaffolding.demo.database.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.scaffolding.demo.database.db.core.DBManager;
import cn.bingoogolapple.scaffolding.demo.database.db.table.GoodsTable;
import cn.bingoogolapple.scaffolding.demo.database.entity.Goods;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2018/1/9
 * 描述:
 */
public class FtsGoodsDao {

    public boolean batchInsert(List<Goods> goodsList) {
        SQLiteDatabase database = null;
        try {
            StringBuilder sqlSb = new StringBuilder();
            sqlSb.append("INSERT INTO " + GoodsTable.TABLE_NAME_GOODS + "(");
            sqlSb.append(GoodsTable.CODE + ",");
            sqlSb.append(GoodsTable.BARCODE + ",");
            sqlSb.append(GoodsTable.NAME + ",");
            sqlSb.append(GoodsTable.UNIT + ",");
            sqlSb.append(GoodsTable.ADDRESS + ",");
            sqlSb.append(GoodsTable.BRAND);
            sqlSb.append(") values(?,?,?,?,?,?)");
            database = DBManager.getInstance().openDatabase();
            SQLiteStatement statement = database.compileStatement(sqlSb.toString());
            database.beginTransaction();
            for (Goods goods : goodsList) {
                statement.bindString(1, goods.getCode());
                statement.bindString(2, goods.getBarcode());
                statement.bindString(3, goods.getName());
                statement.bindString(4, goods.getUnit());
                statement.bindString(5, goods.getAddress());
                statement.bindString(5, goods.getBrand());
                long newlyId = statement.executeInsert();
                if (newlyId == -1) {
                    return false;
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (database != null) {
                    database.endTransaction();
                    DBManager.getInstance().closeDatabase();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public boolean batchInsertFts(List<Goods> goodsList) {
        SQLiteDatabase database = null;
        try {
            StringBuilder sqlSb = new StringBuilder();
            sqlSb.append("INSERT INTO " + GoodsTable.TABLE_NAME_GOODS_FTS + "(");
            sqlSb.append(GoodsTable.CODE + ",");
            sqlSb.append(GoodsTable.BARCODE + ",");
            sqlSb.append(GoodsTable.NAME + ",");
            sqlSb.append(GoodsTable.UNIT + ",");
            sqlSb.append(GoodsTable.ADDRESS + ",");
            sqlSb.append(GoodsTable.BRAND);
            sqlSb.append(") values(?,?,?,?,?,?)");
            database = DBManager.getInstance().openDatabase();
            SQLiteStatement statement = database.compileStatement(sqlSb.toString());
            database.beginTransaction();
            for (Goods goods : goodsList) {
                statement.bindString(1, goods.getCode());
                statement.bindString(2, goods.getBarcode());
                statement.bindString(3, goods.getName());
                statement.bindString(4, goods.getUnit());
                statement.bindString(5, goods.getAddress());
                statement.bindString(5, goods.getBrand());
                long newlyId = statement.executeInsert();
                if (newlyId == -1) {
                    return false;
                }
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (database != null) {
                    database.endTransaction();
                    DBManager.getInstance().closeDatabase();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public List<Goods> query(String keyword) {
        List<Goods> goodsList = new ArrayList<>();
        Cursor cursor = null;
        try {
//            String sql = "SELECT * FROM " + GoodsTable.TABLE_NAME_GOODS + " WHERE " + GoodsTable.NAME + " LIKE '" + keyword + "%'";
//            cursor = DBManager.getInstance().openDatabase().rawQuery(sql, null);

            String sql = "SELECT * FROM " + GoodsTable.TABLE_NAME_GOODS + " WHERE " + GoodsTable.NAME + " LIKE ?";
            cursor = DBManager.getInstance().openDatabase().rawQuery(sql, new String[]{keyword + "%"});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Goods goods = new Goods();
                    goods.setCode(cursor.getString(cursor.getColumnIndex(GoodsTable.CODE)));
                    goods.setBarcode(cursor.getString(cursor.getColumnIndex(GoodsTable.BARCODE)));
                    goods.setName(cursor.getString(cursor.getColumnIndex(GoodsTable.NAME)));
                    goods.setUnit(cursor.getString(cursor.getColumnIndex(GoodsTable.UNIT)));
                    goods.setAddress(cursor.getString(cursor.getColumnIndex(GoodsTable.ADDRESS)));
                    goods.setBrand(cursor.getString(cursor.getColumnIndex(GoodsTable.BRAND)));
                    goodsList.add(goods);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            DBManager.getInstance().closeDatabase();
        }
        return goodsList;
    }

    public List<Goods> queryFts(String keyword) {
        List<Goods> goodsList = new ArrayList<>();
        Cursor cursor = null;
        try {
//            String sql = "SELECT * FROM " + GoodsTable.TABLE_NAME_GOODS_FTS + " WHERE " + GoodsTable.NAME + " MATCH '" + keyword + "*'";
//            cursor = DBManager.getInstance().openDatabase().rawQuery(sql, null);

            String sql = "SELECT * FROM " + GoodsTable.TABLE_NAME_GOODS_FTS + " WHERE " + GoodsTable.NAME + " MATCH ?";
            cursor = DBManager.getInstance().openDatabase().rawQuery(sql, new String[]{keyword + "*"});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Goods goods = new Goods();
                    goods.setCode(cursor.getString(cursor.getColumnIndex(GoodsTable.CODE)));
                    goods.setBarcode(cursor.getString(cursor.getColumnIndex(GoodsTable.BARCODE)));
                    goods.setName(cursor.getString(cursor.getColumnIndex(GoodsTable.NAME)));
                    goods.setUnit(cursor.getString(cursor.getColumnIndex(GoodsTable.UNIT)));
                    goods.setAddress(cursor.getString(cursor.getColumnIndex(GoodsTable.ADDRESS)));
                    goods.setBrand(cursor.getString(cursor.getColumnIndex(GoodsTable.BRAND)));
                    goodsList.add(goods);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            DBManager.getInstance().closeDatabase();
        }
        return goodsList;
    }

    public void clear() {
        int affectedCount = DBManager.getInstance().openDatabase().delete(GoodsTable.TABLE_NAME_GOODS, "1=1", null);
        if (affectedCount > 0) {
            Logger.d("删除了 " + affectedCount + " 条");
        }
        DBManager.getInstance().closeDatabase();
    }

    public void clearFts() {
        int affectedCount = DBManager.getInstance().openDatabase().delete(GoodsTable.TABLE_NAME_GOODS_FTS, "1=1", null);
        if (affectedCount > 0) {
            Logger.d("删除了 " + affectedCount + " 条");
        }
        DBManager.getInstance().closeDatabase();
    }
}
