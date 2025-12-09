package com.fastpay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Fast 易支付 - 启动类
 * 个人免签支付平台，提供便捷的收款解决方案
 * 
 * @author FastPay
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.fastpay.mapper")
public class FastPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastPayApplication.class, args);
        System.out.println("====================================");
        System.out.println("   Fast 易支付 服务启动成功！");
        // System.out.println("   管理后台: http://localhost:3001/fastpay-admin/");
        // System.out.println("   商户平台: http://localhost:3002/fastpay-merchant/");
        System.out.println("   API地址: http://localhost:7001/fastpay-server/api");
        System.out.println("====================================");
    }
}
