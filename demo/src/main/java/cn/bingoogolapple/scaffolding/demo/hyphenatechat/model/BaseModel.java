package cn.bingoogolapple.scaffolding.demo.hyphenatechat.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/13 下午7:29
 * 描述:
 */
public class BaseModel implements Serializable {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    public long id;

}
