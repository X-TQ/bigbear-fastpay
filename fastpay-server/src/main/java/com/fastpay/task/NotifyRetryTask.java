package com.fastpay.task;

import com.fastpay.service.PayOrderService;
import com.fastpay.websocket.PayResultWebSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 回调通知重试定时任务
 * 自动重发失败的回调通知
 *
 * @author FastPay
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class NotifyRetryTask {

    private final PayOrderService payOrderService;

    /**
     * 每分钟执行一次，处理失败的回调通知
     * 重试策略：
     * - 最多重试5次
     * - 每次重试间隔至少1分钟
     * - 重试间隔随次数递增（1分钟、2分钟、4分钟、8分钟、16分钟）
     */
    @Scheduled(fixedRate = 60000)  // 每60秒执行一次
    public void retryFailedNotify() {
        try {
            payOrderService.processFailedNotify();
        } catch (Exception e) {
            log.error("处理失败回调通知异常", e);
        }
    }

    /**
     * 每5分钟执行一次，处理过期订单
     */
    @Scheduled(fixedRate = 300000)  // 每5分钟执行一次
    public void processExpiredOrders() {
        try {
            payOrderService.processExpiredOrders();
        } catch (Exception e) {
            log.error("处理过期订单异常", e);
        }
    }

    /**
     * 处理ws连接占用(关闭已超期的订单ws连接)
     */
    @Scheduled(fixedRate = 200000)  // 每2分钟执行一次
    public void processWsConnect() {
        try {
            // 超期未支付的订单,关闭ws连接
            PayResultWebSocket.closePayTimeOut();
        } catch (Exception e) {
            log.error("处理过期订单异常", e);
        }
    }
}
