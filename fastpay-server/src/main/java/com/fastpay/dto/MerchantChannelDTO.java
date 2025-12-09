package com.fastpay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 商户通道 DTO
 *
 * @author FastPay
 */
@Data
public class MerchantChannelDTO {

    /**
     * 通道ID（更新时使用）
     */
    private Long id;

    /**
     * 商户ID（管理后台创建时使用）
     */
    private Long merchantId;

    /**
     * 通道名称
     */
    @NotBlank(message = "通道名称不能为空")
    private String channelName;

    /**
     * 支付类型：wxpay-微信，alipay-支付宝
     */
    @NotBlank(message = "支付类型不能为空")
    private String payType;

    /**
     * 通道消息模版
     */
    private String template;

    /**
     * 备注
     */
    private String remark;
}
