package com.fastpay.controller.notify;

import com.fastpay.common.Result;
import com.fastpay.dto.PayNotifyDTO;
import com.fastpay.service.PayNotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付通知回调控制器
 * 接收监听软件推送的微信/支付宝收款通知，自动匹配并确认订单支付
 *
 * 一、微信收款通知示例：
 * {
 *   "type": 1,
 *   "packageName": "com.tencent.mm",
 *   "title": "微信收款助手",
 *   "msg": "com.tencent.mm\n[8条]微信收款助手: 微信支付收款0.01元(朋友到店)\n微信收款助手\nUID：10316\n2025-12-04 10:01:15\nXiaomi 14",
 *   "receiveTime": "2025-12-04 10:01:15",
 *   "timestamp": "1733281275000",
 *   "sign": "xxx"
 * }
 *
 * 二、支付宝收款通知示例：
 * {
 *   "type": 2,
 *   "packageName": "com.eg.android.AlipayGphone",
 *   "title": "你已成功收款0.01元（新顾客消费）",
 *   "msg": "com.eg.android.AlipayGphone\n已转入余额 茅台戴森限时开抢中>>\n你已成功收款0.01元（新顾客消费）\nUID：10298\n2025-12-04 11:02:09\nXiaomi 14",
 *   "receiveTime": "2025-12-04 11:02:09",
 *   "timestamp": "1733284929000",
 *   "sign": "xxx"
 * }
 *
 * @author FastPay
 */
@Slf4j
@Tag(name = "支付通知回调", description = "接收监听软件推送的支付通知,进行转发给对应的商户")
@RestController
@RequestMapping("/api/notify")
@RequiredArgsConstructor
public class NotifyController {

    private final PayNotifyService payNotifyService;


    /**
     * 支付成功通知回调接口
     * 接收监听软件推送的微信/支付宝收款通知
     *
     * @param notifyDTO 通知内容
     * @return 处理结果
     */
    @PostMapping("/callback")
    @Operation(summary = "支付通知回调", description = "接收监听软件推送的微信/支付宝收款通知")
    public Result<Void> callback(@RequestBody PayNotifyDTO notifyDTO) {
        log.info("【接收到支付通知】channelId={}, title={}, receiveTime={}",
                notifyDTO.getChannelId(), notifyDTO.getTitle(), notifyDTO.getReceiveTime());
        log.debug("支付通知详情：{}", notifyDTO);
        return payNotifyService.callback(notifyDTO);
    }

}
