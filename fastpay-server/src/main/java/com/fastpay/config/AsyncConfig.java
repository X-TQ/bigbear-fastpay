package com.fastpay.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池配置
 * 根据服务器CPU和内存资源配置合适的线程池参数
 *
 * @author FastPay
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 支付通知线程池
     * 用于异步处理商户回调通知，提高响应速度
     */
    @Bean("payNotifyExecutor")
    public Executor payNotifyExecutor() {
        // 获取CPU核心数
        int cpuCores = Runtime.getRuntime().availableProcessors();
        // 获取最大可用内存（MB）
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        
        // 根据CPU核心数和内存动态计算线程池参数
        // 核心线程数：CPU核心数 * 2（IO密集型任务）
        int corePoolSize = Math.max(4, cpuCores * 2);
        // 最大线程数：核心线程数 * 2，但不超过50
        int maxPoolSize = Math.min(50, corePoolSize * 2);
        // 队列容量：根据内存大小调整，每100MB内存分配100个队列位置，最小500，最大2000
        int queueCapacity = Math.min(2000, Math.max(500, (int) (maxMemory / 100) * 100));
        
        log.info("初始化支付通知线程池 - CPU核心数: {}, 最大内存: {}MB, 核心线程: {}, 最大线程: {}, 队列容量: {}",
                cpuCores, maxMemory, corePoolSize, maxPoolSize, queueCapacity);
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 队列容量
        executor.setQueueCapacity(queueCapacity);
        // 线程名前缀
        executor.setThreadNamePrefix("pay-notify-");
        // 线程空闲时间（秒）
        executor.setKeepAliveSeconds(60);
        // 允许核心线程超时
        executor.setAllowCoreThreadTimeOut(true);
        // 拒绝策略：由调用线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后再关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        return executor;
    }

    /**
     * WebSocket通知线程池
     * 用于快速发送WebSocket消息，让用户立即收到支付结果
     * 配置更小的队列和更快的响应
     */
    @Bean("wsNotifyExecutor")
    public Executor wsNotifyExecutor() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        
        // WebSocket通知需要快速响应，使用较小的线程池但更快的处理
        int corePoolSize = Math.max(2, cpuCores);
        int maxPoolSize = Math.max(4, cpuCores * 2);
        
        log.info("初始化WebSocket通知线程池 - 核心线程: {}, 最大线程: {}", corePoolSize, maxPoolSize);
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        // 较小的队列，避免消息积压
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ws-notify-");
        executor.setKeepAliveSeconds(30);
        executor.setAllowCoreThreadTimeOut(true);
        // 拒绝时由调用线程执行，确保消息不丢失
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        
        executor.initialize();
        return executor;
    }
}
