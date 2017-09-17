package cn.bingoogolapple.scaffolding.demo.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2017/9/17
 * 描述:
 */
@Entity
public class ProductOrder {
    @Id
    private Long id;
    private Long productId;
    private Long orderId;
    @Generated(hash = 293064453)
    public ProductOrder(Long id, Long productId, Long orderId) {
        this.id = id;
        this.productId = productId;
        this.orderId = orderId;
    }
    @Generated(hash = 1312206183)
    public ProductOrder() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getProductId() {
        return this.productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Long getOrderId() {
        return this.orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
