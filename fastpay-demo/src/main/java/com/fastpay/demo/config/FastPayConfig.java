package com.fastpay.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * FAST 易支付配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "fastpay")
public class FastPayConfig {

    /**
     * 商户编号
     */
    private String merchantNo;

    /**
     * 店铺编号
     */
    private String shopNo;

    /**
     * API 密钥
     */
    private String apiSecret;

    /**
     * 支付网关地址
     */
    private String gatewayUrl;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 同步跳转地址
     */
    private String returnUrl;


    /**
     * 获取页面跳转支付地址
     */
    public String getSubmitUrl() {
        return gatewayUrl + "/api/pay/submit";
    }

    /**
     * 获取 API 创建订单地址
     */
    public String getCreateUrl() {
        return gatewayUrl + "/api/pay/create";
    }

    /**
     * 获取订单查询地址
     */
    public String getQueryUrl() {
        return gatewayUrl + "/api/pay/query";
    }
}
