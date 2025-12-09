package com.fastpay.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付结果 VO
 *
 * @author FastPay
 */
@Data
public class PayResultVO {

    /**
     * 平台订单号
     */
    private String orderNo;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 支付类型
     */
    private String payType;

    /**
     * 支付方式
     */
    private String payMethod;

    /**
     * 二维码图片URL（API支付时返回）
     */
    private String qrcodeUrl;

    /**
     * 支付页面URL（页面跳转支付时返回）
     */
    private String payPageUrl;

    /**
     * 订单过期时间（时间戳，秒）
     */
    private Long expireTime;
}
