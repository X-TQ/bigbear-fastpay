package com.fastpay.dto;

import lombok.Data;

/**
 * 支付通知DTO
 * 用于接收监听软件推送的支付通知
 *
 * @author FastPay
 */
@Data
public class PayNotifyDTO {

    /**
     * 商户id（主键id）
     */
    private Long channelId;

    /**
     * 应用包名，用于区分支付类型
     * com.tencent.mm: 微信
     * com.eg.android.AlipayGphone: 支付宝
     */
    private String packageName;

    /**
     * 通知标题
     * 微信收款时为"微信收款助手"
     * 支付宝收款时为"你已成功收款X.XX元（新顾客消费）"
     */
    private String title;

    /**
     * 通知消息内容
     * 微信示例：com.tencent.mm\n[8条]微信收款助手: 微信支付收款0.01元(朋友到店)\n...
     * 支付宝示例：com.eg.android.AlipayGphone\n已转入余额...\n你已成功收款0.01元...
     */
    private String msg;

    /**
     * 监听软件收到支付通知的时间
     * 格式：yyyy-MM-dd HH:mm:ss
     * 示例：2025-12-04 10:01:15
     */
    private String receiveTime;

    /**
     * 时间戳（毫秒）
     */
    private String timestamp;

    /**
     * 签名
     * 签名算法：HmacSHA256(timestamp + "\n" + secret, secret)
     */
    private String sign;
}
