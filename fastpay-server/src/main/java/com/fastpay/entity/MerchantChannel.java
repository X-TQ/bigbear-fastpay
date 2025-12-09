package com.fastpay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商户通道配置类
 * 存储商户的发送通道
 * 通道指的是：支付宝、微信、第三方聚合支付平台
 *
 * @author FastPay
 */
@Data
@TableName("fp_merchant_channel")
public class MerchantChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通道id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 通道所属商户id
     */
    private Long merchantId;

    /**
     * 支付类型：wxpay-微信，alipay-支付宝
     */
    private String payType;

    /**
     * 通道启用状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 通道消息模版
     * 监听软件监听到支付成功信息后，会替换模版占位符内容，回调 NotifyController.callback
     * 模版示例：
     * {"channelId":"[channel_id]","title":"[title]","msg":"[msg]","receiveTime":"[receive_time]","timestamp":"[timestamp]","sign":"[sign]"}
     */
    private String template;

    /**
     * 备注
     */
    private String remark;

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

    /**
     * 商户名称（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    private String merchantName;
}
