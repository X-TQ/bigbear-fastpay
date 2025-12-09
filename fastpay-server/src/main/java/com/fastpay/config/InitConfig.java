package com.fastpay.config;

import com.fastpay.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 系统初始化配置
 * 在应用启动时执行初始化操作
 *
 * @author FastPay
 */
@Slf4j
@Component
public class InitConfig implements CommandLineRunner {

    private final AdminService adminService;

    public InitConfig(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void run(String... args) {
        // 初始化默认管理员账号
        try {
            adminService.initDefaultAdmin();
        } catch (Exception e) {
            log.warn("初始化管理员账号失败（可能数据库未就绪）: {}", e.getMessage());
        }
    }
}
