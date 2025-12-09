package com.fastpay.vo;

import lombok.Data;

/**
 * 登录响应 VO
 *
 * @author FastPay
 */
@Data
public class LoginVO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称/商户名称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户类型：admin/merchant
     */
    private String userType;

    /**
     * JWT Token
     */
    private String token;

    /**
     * 商户编号（商户登录时返回）
     */
    private String merchantNo;

    /**
     * API Key（商户登录时返回）
     */
    private String apiKey;
}
