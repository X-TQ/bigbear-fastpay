package com.fastpay.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 控制台统计数据 VO
 *
 * @author FastPay
 */
@Data
public class DashboardVO {

    /**
     * 今日订单数
     */
    private Integer todayOrderCount;

    /**
     * 今日成功订单数
     */
    private Integer todaySuccessCount;

    /**
     * 今日交易金额
     */
    private BigDecimal todayAmount;

    /**
     * 总订单数
     */
    private Long totalOrderCount;

    /**
     * 总成功订单数
     */
    private Long totalSuccessCount;

    /**
     * 总交易金额
     */
    private BigDecimal totalAmount;

    /**
     * 商户总数
     */
    private Long merchantCount;

    /**
     * 店铺总数
     */
    private Long shopCount;

    /**
     * 二维码总数
     */
    private Long qrcodeCount;

    /**
     * 微信支付订单数
     */
    private Long wxpayCount;

    /**
     * 微信支付金额
     */
    private BigDecimal wxpayAmount;

    /**
     * 支付宝订单数
     */
    private Long alipayCount;

    /**
     * 支付宝金额
     */
    private BigDecimal alipayAmount;

    /**
     * 最近7天订单统计
     */
    private List<Map<String, Object>> recentStats;
}
