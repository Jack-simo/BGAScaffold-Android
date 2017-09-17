package cn.bingoogolapple.scaffolding.demo.greendao.activity;

import android.os.Bundle;

import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.List;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.demo.databinding.ActivityGreendaoBinding;
import cn.bingoogolapple.scaffolding.demo.db.CustomerDao;
import cn.bingoogolapple.scaffolding.demo.db.OrderDao;
import cn.bingoogolapple.scaffolding.demo.db.ProductDao;
import cn.bingoogolapple.scaffolding.demo.db.ProductOrderDao;
import cn.bingoogolapple.scaffolding.demo.greendao.entity.Customer;
import cn.bingoogolapple.scaffolding.demo.greendao.entity.Order;
import cn.bingoogolapple.scaffolding.demo.greendao.util.GreenDaoUtil;
import cn.bingoogolapple.scaffolding.view.MvcBindingActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2017/9/17
 * 描述:
 */
public class GreenDaoActivity extends MvcBindingActivity<ActivityGreendaoBinding> {
    private CustomerDao mCustomerDao;
    private OrderDao mOrderDao;
    private ProductDao mProductDao;
    private ProductOrderDao mProductOrderDao;

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_greendao;
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mCustomerDao = GreenDaoUtil.getCustomerDao();
        mOrderDao = GreenDaoUtil.getOrderDao();
        mProductDao = GreenDaoUtil.getProductDao();
        mProductOrderDao = GreenDaoUtil.getProductOrderDao();
    }

    public void addCustomer() {
        Customer customer1 = new Customer();
        customer1.setName("顾客1");
        mCustomerDao.insert(customer1);

        Customer customer2 = new Customer();
        customer2.setName("顾客2");
        mCustomerDao.insert(customer2);
    }

    public void queryCustomer() {
        List<Customer> customerList = mCustomerDao.queryBuilder().build().list();
        List<Order> orderList = customerList.get(0).getOrderList();
        Logger.d(orderList);
    }

    public void testInsertRowId() {
        Order order;
        for (int i = 1; i < 5; i++) {
            order = new Order();
            order.setName("订单" + i);
            order.setCreateAt(new Date());
            order.setCustomerId(1);
            mOrderDao.insert(order);
        }
        List<Order> orderList = mOrderDao.loadAll();
        order = orderList.get(0);
        order = mOrderDao.load(1L);
        order = mOrderDao.loadByRowId(1);

        mOrderDao.deleteByKey(1L);
//        mOrderDao.deleteAll();

        orderList = mOrderDao.loadAll();
        order = orderList.get(0);
        order = mOrderDao.load(1L);
        order = mOrderDao.loadByRowId(1);

        order = new Order();
        order.setName("订单新增的");
        order.setCreateAt(new Date());
        order.setCustomerId(1);
        // 数据没清空时，rowId 是之前的最大值加 1，数据清空后就是 1（相当于还是之前最大值 0 + 1）
        long orderId = mOrderDao.insert(order);

        orderList = mOrderDao.loadAll();
        order = mOrderDao.load(orderId);
        order = mOrderDao.loadByRowId(orderId);

        Logger.d(order);
    }
}
