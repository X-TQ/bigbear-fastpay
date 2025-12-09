package com.fastpay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单实体类
 * 存储所有支付订单信息
 *
 * @author FastPay
 */
@Data
@TableName("fp_pay_order")
public class PayOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 平台订单号
     */
    private String orderNo;

    /**
     * 商户订单号（商户系统的订单号）
     */
    private String outTradeNo;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 使用的二维码ID
     */
    private Long qrcodeId;

    /**
     * 支付类型：wxpay-微信，alipay-支付宝
     */
    private String payType;

    /**
     * 支付方式：page-页面跳转，api-接口调用
     */
    private String payMethod;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 实际支付金额
     */
    private BigDecimal payAmount;

    /**
     * 商品名称/订单描述
     */
    private String subject;

    /**
     * 订单状态：0-待支付，1-已支付，2-已过期，3-已关闭
     */
    private Integer status;

    /**
     * 回调通知地址
     */
    private String notifyUrl;

    /**
     * 支付成功跳转地址
     */
    private String returnUrl;

    /**
     * 回调通知状态：0-未通知，1-通知成功，2-通知失败
     */
    private Integer notifyStatus;

    /**
     * 回调通知次数
     */
    private Integer notifyCount;

    /**
     * 最后通知时间
     */
    private LocalDateTime lastNotifyTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 扩展参数（JSON格式）
     */
    private String extParam;

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
     * 商户名称（非数据库字段）
     */
    @TableField(exist = false)
    private String merchantName;

    /**
     * 店铺名称（非数据库字段）
     */
    @TableField(exist = false)
    private String shopName;
}
