package com.fastpay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 商户信息 DTO
 *
 * @author FastPay
 */
@Data
public class MerchantDTO {

    /**
     * 商户ID（更新时使用）
     */
    private Long id;

    /**
     * 商户名称
     */
    @NotBlank(message = "商户名称不能为空")
    private String merchantName;

    /**
     * 登录用户名
     */
    @NotBlank(message = "登录用户名不能为空")
    private String username;

    /**
     * 登录密码（新增时必填）
     */
    private String password;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人不能为空")
    private String contactName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 支付成功回调地址
     */
    private String notifyUrl;

    /**
     * 支付成功跳转地址
     */
    private String returnUrl;

    /**
     * 备注
     */
    private String remark;
}
