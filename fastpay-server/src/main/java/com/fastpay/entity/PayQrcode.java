package com.fastpay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收款二维码实体类
 * 存储商户上传的个人收款二维码信息
 *
 * @author FastPay
 */
@Data
@TableName("fp_pay_qrcode")
public class PayQrcode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属商户ID
     */
    private Long merchantId;

    /**
     * 配置的通道id
     */
    private String channelId;

    /**
     * 所属店铺ID
     */
    private Long shopId;

    /**
     * 支付类型(冗余字段，等于商户通道里的payType)：wxpay-微信，alipay-支付宝
     */
    private String payType;

    /**
     * 二维码名称/备注
     */
    private String qrcodeName;

    /**
     * 二维码图片URL
     */
    private String qrcodeUrl;

    /**
     * 累计收款金额
     */
    private BigDecimal totalAmount;

    /**
     * 累计收款笔数
     */
    private Integer totalCount;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 排序权重（越大越优先）
     */
    private Integer sortWeight;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 店铺名称（非数据库字段）
     */
    @TableField(exist = false)
    private String shopName;
}
