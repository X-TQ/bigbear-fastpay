package com.fastpay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户实体类
 * 存储商户（有个人收款需求的商家）信息
 *
 * @author FastPay
 */
@Data
@TableName("fp_merchant")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户编号（唯一标识，用于API调用）
     */
    private String merchantNo;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录密码（加密存储）
     */
    private String password;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * API密钥（用于接口签名验证）
     */
    private String apiKey;

    /**
     * API密钥Secret
     */
    private String apiSecret;

    /**
     * 支付成功回调地址(用户支付成功后，Fast易支付平台会将支付结果回推给商户对接方的系统)
     */
    private String notifyUrl;

    /**
     * 支付成功跳转地址(默认商户支付成功跳转地址,创建订单时传了returnUrl，会以后传入的为准)
     */
    private String returnUrl;

    /**
     * 累计交易金额
     */
    private BigDecimal totalAmount;

    /**
     * 累计交易笔数
     */
    private Integer totalCount;

    /**
     * 状态：0-禁用，1-启用，2-待审核
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
