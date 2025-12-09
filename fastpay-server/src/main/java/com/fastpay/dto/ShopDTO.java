package com.fastpay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 店铺信息 DTO
 *
 * @author FastPay
 */
@Data
public class ShopDTO {

    /**
     * 店铺ID（更新时使用）
     */
    private Long id;

    /**
     * 所属商户ID（管理员创建时使用）
     */
    private Long merchantId;

    /**
     * 店铺名称
     */
    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    /**
     * 店铺描述
     */
    private String description;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;
}
