package cn.bingoogolapple.scaffolding.demo.database.db.util;

import android.content.Context;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import cn.bingoogolapple.scaffolding.demo.db.DaoMaster;
import cn.bingoogolapple.scaffolding.demo.db.DaoSession;
import cn.bingoogolapple.scaffolding.demo.db.GoodsDao;
import cn.bingoogolapple.scaffolding.util.AppManager;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2017/9/17
 * 描述:
 */
public class GreenDaoUtil {
    private static DaoSession sDaoSession;

    private GreenDaoUtil() {
    }

    public static void initGreenDao() {
        DBOpenHelper openHelper = new DBOpenHelper(AppManager.getApp(), "greendao.db");
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDb());
//        DaoMaster daoMaster = new DaoMaster(openHelper.getEncryptedWritableDb("your_pwd"));
        sDaoSession = daoMaster.newSession();

        QueryBuilder.LOG_SQL = AppManager.getInstance().isBuildDebug();
        QueryBuilder.LOG_VALUES = AppManager.getInstance().isBuildDebug();
    }

    public static GoodsDao getGoodsDao() {
        return sDaoSession.getGoodsDao();
    }

    private static class DBOpenHelper extends DaoMaster.OpenHelper {

        public DBOpenHelper(Context context, String name) {
            super(context, name);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            // TODO
        }
    }
}
