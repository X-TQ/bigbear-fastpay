package com.fastpay.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 收款二维码 DTO
 *
 * @author FastPay
 */
@Data
public class PayQrcodeDTO {

    /**
     * 二维码ID（更新时使用）
     */
    private Long id;

    /**
     * 所属店铺ID
     */
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    /**
     * 通道ID
     */
    @NotNull(message = "通道ID不能为空")
    private Long channelId;

    /**
     * 支付类型：wxpay-微信，alipay-支付宝（冗余字段，由通道决定）
     */
    private String payType;

    /**
     * 二维码名称/备注
     */
    private String qrcodeName;

    /**
     * 二维码图片URL(前台解析出来的)
     */
    private String qrcodeUrl;

    /**
     * 排序权重
     */
    private Integer sortWeight;
}
