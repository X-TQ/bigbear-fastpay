package com.fastpay.service;

import com.fastpay.common.Result;
import com.fastpay.dto.BizCallbackDTO;
import com.fastpay.dto.PayNotifyDTO;

import java.math.BigDecimal;

/**
 * 支付通知服务接口
 * 处理监听软件推送的支付通知
 *
 * @author FastPay
 */
public interface PayNotifyService {

    /**
     * 处理支付通知
     *
     * @param bizCallbackDTO 通知内容
     * @return 处理结果
     */
    boolean handleNotify(BizCallbackDTO bizCallbackDTO);

    /**
     * 处理微信收款通知
     *
     * @param msg         消息内容
     * @param receiveTime 接收时间
     * @return 是否处理成功
     */
    boolean handleWxPayNotify(BizCallbackDTO  bizCallbackDTO);

    /**
     * 处理支付宝收款通知
     *
     * @param title       通知标题
     * @param msg         消息内容
     * @param receiveTime 接收时间
     * @return 是否处理成功
     */
    boolean handleAlipayNotify(BizCallbackDTO  bizCallbackDTO);

    /**
     * 解析微信收款金额
     *
     * @param msg 消息内容
     * @return 金额，解析失败返回null
     */
    BigDecimal parseWxPayAmount(String msg);

    /**
     * 解析支付宝收款金额
     *
     * @param title 通知标题
     * @return 金额，解析失败返回null
     */
    BigDecimal parseAlipayAmount(String title);

    /**
     * 验证签名
     *
     * @param timestamp 时间戳
     * @param sign      签名
     * @param secret    密钥
     * @return 是否验证通过
     */
    boolean verifySign(String timestamp, String sign, String secret);

    /**
     * 生成签名
     *
     * @param timestamp 时间戳
     * @param secret    密钥
     * @return 签名
     */
    String generateSign(String timestamp, String secret);

    /**
     * 支付成功通知回调接口 - 将通知转发给对应商户的回调接口
     * 接收监听软件推送的微信/支付宝收款通知
     *
     * @param notifyDTO 通知内容
     * @return 处理结果
     */
    Result<Void> callback(PayNotifyDTO notifyDTO);
}
