package cn.bingoogolapple.scaffolding.demo.greendao.util;

import android.content.Context;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import cn.bingoogolapple.scaffolding.demo.db.CustomerDao;
import cn.bingoogolapple.scaffolding.demo.db.DaoMaster;
import cn.bingoogolapple.scaffolding.demo.db.DaoSession;
import cn.bingoogolapple.scaffolding.demo.db.OrderDao;
import cn.bingoogolapple.scaffolding.demo.db.ProductDao;
import cn.bingoogolapple.scaffolding.demo.db.ProductOrderDao;
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

    public static OrderDao getOrderDao() {
        return sDaoSession.getOrderDao();
    }

    public static CustomerDao getCustomerDao() {
        return sDaoSession.getCustomerDao();
    }

    public static ProductDao getProductDao() {
        return sDaoSession.getProductDao();
    }

    public static ProductOrderDao getProductOrderDao() {
        return sDaoSession.getProductOrderDao();
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
