package cn.bingoogolapple.scaffolding.demo.database.activity;

import android.content.res.Resources;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.database.db.dao.FtsGoodsDao;
import cn.bingoogolapple.scaffolding.demo.database.db.util.GreenDaoUtil;
import cn.bingoogolapple.scaffolding.demo.database.entity.Goods;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityDatabaseBinding;
import cn.bingoogolapple.scaffolding.demo.db.GoodsDao;
import cn.bingoogolapple.scaffolding.util.AppManager;
import cn.bingoogolapple.scaffolding.util.GsonUtil;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2018/1/9
 * 描述:
 */
public class DatabaseActivity extends MvcBindingActivity<ActivityDatabaseBinding> {
    private FtsGoodsDao mFtsGoodsDao;

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_database;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mFtsGoodsDao = new FtsGoodsDao();
    }

    public void query() {
        long startTime = System.currentTimeMillis();
        List<Goods> goodsList = mFtsGoodsDao.query(mBinding.etFtsKeyword.getText().toString().trim());
        long endTime = System.currentTimeMillis();
        StringBuilder resultSb = new StringBuilder();
        resultSb.append("query：" + goodsList.size());
        resultSb.append("\n耗时：" + (endTime - startTime) + "ms");
        resultSb.append("\n耗时：" + (endTime - startTime) * 1.0 / 1000 + "s");
        mBinding.tvResult.setText(resultSb.toString());
    }

    public void queryFts() {
        long startTime = System.currentTimeMillis();
        List<Goods> goodsList = mFtsGoodsDao.queryFts(mBinding.etFtsKeyword.getText().toString().trim());
        long endTime = System.currentTimeMillis();
        StringBuilder resultSb = new StringBuilder();
        resultSb.append("queryFts：" + goodsList.size());
        resultSb.append("\n耗时：" + (endTime - startTime) + "ms");
        resultSb.append("\n耗时：" + (endTime - startTime) * 1.0 / 1000 + "s");
        mBinding.tvResult.setText(resultSb.toString());
    }

    public void queryGreenDao() {
        long startTime = System.currentTimeMillis();
        List<Goods> goodsList = GreenDaoUtil.getGoodsDao()
                .queryBuilder()
                .where(GoodsDao.Properties.Name.like("%" + mBinding.etFtsKeyword.getText().toString().trim() + "%"))
                .build()
                .list();
        long endTime = System.currentTimeMillis();
        StringBuilder resultSb = new StringBuilder();
        resultSb.append("queryGreenDao：" + goodsList.size());
        resultSb.append("\n耗时：" + (endTime - startTime) + "ms");
        resultSb.append("\n耗时：" + (endTime - startTime) * 1.0 / 1000 + "s");
        mBinding.tvResult.setText(resultSb.toString());
    }



    public void batchInsert() {
        Resources resources = AppManager.getApp().getResources();
        InputStream inputStream = resources.openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            List<Goods> goodsList = GsonUtil.getInstance().getGson().fromJson(new JsonReader(reader), new TypeToken<List<Goods>>() {
            }.getType());

            long startTime = System.currentTimeMillis();
            mFtsGoodsDao.batchInsert(goodsList);
            long endTime = System.currentTimeMillis();
            StringBuilder resultSb = new StringBuilder();
            resultSb.append("batchInsert：" + goodsList.size());
            resultSb.append("\n耗时：" + (endTime - startTime) + "ms");
            resultSb.append("\n耗时：" + (endTime - startTime) * 1.0 / 1000 + "s");
            mBinding.tvResult.setText(resultSb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void batchInsertFts() {
        Resources resources = AppManager.getApp().getResources();
        InputStream inputStream = resources.openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            List<Goods> goodsList = GsonUtil.getInstance().getGson().fromJson(new JsonReader(reader), new TypeToken<List<Goods>>() {
            }.getType());

            long startTime = System.currentTimeMillis();
            mFtsGoodsDao.batchInsertFts(goodsList);
            long endTime = System.currentTimeMillis();
            StringBuilder resultSb = new StringBuilder();
            resultSb.append("batchInsertFts：" + goodsList.size());
            resultSb.append("\n耗时：" + (endTime - startTime) + "ms");
            resultSb.append("\n耗时：" + (endTime - startTime) * 1.0 / 1000 + "s");
            mBinding.tvResult.setText(resultSb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void batchInsertGreenDao() {
        Resources resources = AppManager.getApp().getResources();
        InputStream inputStream = resources.openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            List<Goods> goodsList = GsonUtil.getInstance().getGson().fromJson(new JsonReader(reader), new TypeToken<List<Goods>>() {
            }.getType());

            long startTime = System.currentTimeMillis();
            GreenDaoUtil.getGoodsDao().insertInTx(goodsList);
            long endTime = System.currentTimeMillis();
            StringBuilder resultSb = new StringBuilder();
            resultSb.append("batchInsertGreenDao：" + goodsList.size());
            resultSb.append("\n耗时：" + (endTime - startTime) + "ms");
            resultSb.append("\n耗时：" + (endTime - startTime) * 1.0 / 1000 + "s");
            mBinding.tvResult.setText(resultSb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        long startTime = System.currentTimeMillis();
        mFtsGoodsDao.clear();
        long endTime = System.currentTimeMillis();
        StringBuilder resultSb = new StringBuilder();
        resultSb.append("clear");
        resultSb.append("\n耗时：" + (endTime - startTime) + "ms");
        resultSb.append("\n耗时：" + (endTime - startTime) * 1.0 / 1000 + "s");
        mBinding.tvResult.setText(resultSb.toString());
    }

    public void clearFts() {
        long startTime = System.currentTimeMillis();
        mFtsGoodsDao.clearFts();
        long endTime = System.currentTimeMillis();
        StringBuilder resultSb = new StringBuilder();
        resultSb.append("clearFts");
        resultSb.append("\n耗时：" + (endTime - startTime) + "ms");
        resultSb.append("\n耗时：" + (endTime - startTime) * 1.0 / 1000 + "s");
        mBinding.tvResult.setText(resultSb.toString());
    }

    public void clearGreenDao() {
        long startTime = System.currentTimeMillis();
        GreenDaoUtil.getGoodsDao().deleteAll();
        long endTime = System.currentTimeMillis();
        StringBuilder resultSb = new StringBuilder();
        resultSb.append("clearGreenDao");
        resultSb.append("\n耗时：" + (endTime - startTime) + "ms");
        resultSb.append("\n耗时：" + (endTime - startTime) * 1.0 / 1000 + "s");
        mBinding.tvResult.setText(resultSb.toString());
    }
}
