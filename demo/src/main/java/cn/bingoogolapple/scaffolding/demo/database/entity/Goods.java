package cn.bingoogolapple.scaffolding.demo.database.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2018/1/9
 * 描述:
 */
@Entity
public class Goods {

    /**
     * id : 3644
     * barcode : 6922623625269
     * name : 浪莎第3代天鹅绒短袜
     * unit : 双
     * skuId : 2215
     * costPrice : 0
     * sellingPrice : 0
     * address : 浙江义乌
     * manufacturer :
     * brand : 天鹅
     * expirationDate :
     * code : lsd3dterdw
     * industry :
     * categoryName :
     */

    @Id
    private Long id;
    private String barcode;
    private String name;
    private String unit;
    private String skuId;
    private String costPrice;
    private String sellingPrice;
    private String address;
    private String manufacturer;
    private String brand;
    private String expirationDate;
    private String code;
    private String industry;
    private String categoryName;
    
    @Generated(hash = 1770709345)
    public Goods() {
    }
    @Generated(hash = 1344937165)
    public Goods(Long id, String barcode, String name, String unit, String skuId,
            String costPrice, String sellingPrice, String address,
            String manufacturer, String brand, String expirationDate, String code,
            String industry, String categoryName) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.unit = unit;
        this.skuId = skuId;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.address = address;
        this.manufacturer = manufacturer;
        this.brand = brand;
        this.expirationDate = expirationDate;
        this.code = code;
        this.industry = industry;
        this.categoryName = categoryName;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBarcode() {
        return this.barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUnit() {
        return this.unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getSkuId() {
        return this.skuId;
    }
    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }
    public String getCostPrice() {
        return this.costPrice;
    }
    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }
    public String getSellingPrice() {
        return this.sellingPrice;
    }
    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getManufacturer() {
        return this.manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getBrand() {
        return this.brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getExpirationDate() {
        return this.expirationDate;
    }
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getIndustry() {
        return this.industry;
    }
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    public String getCategoryName() {
        return this.categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    
}
