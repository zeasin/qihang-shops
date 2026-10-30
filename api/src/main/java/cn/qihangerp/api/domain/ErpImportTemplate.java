package cn.qihangerp.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单导入表头映射模板
 * @TableName erp_import_template
 */
@TableName(value = "erp_import_template")
@Data
public class ErpImportTemplate implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 平台 1淘宝天猫 2京东 3拼多多 4抖店 5微信小店 9其他
     */
    private Integer platform;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 映射JSON：系统字段→Excel列索引
     */
    private String mappingJson;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
