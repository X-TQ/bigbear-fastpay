package com.fastpay.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建支付订单 DTO
 * 用于商户调用支付接口时的参数
 *
 * @author FastPay
 */
@Data
public class CreateOrderDTO {

    /**
     * 商户编号
     */
    @NotBlank(message = "商户编号不能为空")
    private String merchantNo;

    /**
     * 商品订单号
     */
    @NotBlank(message = "商户订单号不能为空")
    private String outTradeNo;

    /**
     * 店铺编号不能为空
     */
    @NotBlank(message = "店铺编号不能为空")
    private String shopNo;

    /**
     * 订单金额（元）
     */
    @NotNull(message = "订单金额不能为空")
    @DecimalMin(value = "0.01", message = "订单金额最小为0.01元")
    private BigDecimal amount;

    /**
     * 商品名称/订单描述
     */
    @NotBlank(message = "商品名称不能为空")
    private String subject;

    /**
     * 支付类型：wxpay-微信，alipay-支付宝
     */
    @NotBlank(message = "支付类型不能为空")
    private String payType;

    /**
     * 支付方式：page-页面跳转，api-接口调用 (不需要传,create、submit接口内部会自动设置对应的支付方式)
     */
    private String payMethod;

    /**
     * 扩展参数（JSON格式，回调时原样返回）
     */
    private String extParam;

    /**
     * 签名(MD5签名)
     */
    @NotBlank(message = "签名不能为空")
    private String sign;

    /**
     * 时间戳（秒）
     */
    @NotNull(message = "时间戳不能为空")
    private Long timestamp;

    /**
     * 支付成功后跳转地址
     */
    String returnUrl;
}
