package com.fastpay.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FAST 易支付 - Java 对接 Demo 启动类
 */
@SpringBootApplication
public class FastPayDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastPayDemoApplication.class, args);
        System.out.println("========================================");
        System.out.println("  FAST 易支付 Demo 启动成功！");
        System.out.println("  访问地址: http://localhost:7002");
        System.out.println("========================================");
    }
}
