package cn.bingoogolapple.scaffolding.demo.database.db.table;

import android.provider.BaseColumns;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2018/1/9
 * 描述:
 */
public interface GoodsTable extends BaseColumns {
    String TABLE_NAME_GOODS_FTS = "t_goods_fts";
    String TABLE_NAME_GOODS = "t_goods";
    // BaseColumns接口默认添加了列_id
    String CODE = "code";
    String BARCODE = "barcode";
    String NAME = "name";
    String UNIT = "unit";
    String ADDRESS = "address";
    String BRAND = "brand";
}