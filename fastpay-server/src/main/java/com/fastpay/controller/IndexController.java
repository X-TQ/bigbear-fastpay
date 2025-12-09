package com.fastpay.controller;

import com.fastpay.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 * 提供API服务状态检查
 *
 * @author FastPay
 */
@Tag(name = "系统接口", description = "系统状态检查相关接口")
@RestController
public class IndexController {

    /**
     * API 根路径 - 服务状态检查
     */
    @Operation(summary = "服务状态", description = "检查API服务运行状态")
    @GetMapping({"/", "/api", "/api/"})
    public Result<Map<String, Object>> index() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Fast 易支付 API");
        data.put("version", "1.0.0");
        data.put("status", "running");
        data.put("timestamp", System.currentTimeMillis());
        return Result.success("服务运行正常", data);
    }

    /**
     * 健康检查接口
     */
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    @GetMapping("/api/health")
    public Result<String> health() {
        return Result.success("OK");
    }

    /**
     * API 文档说明
     */
    @Operation(summary = "接口信息", description = "获取API接口列表信息")
    @GetMapping("/api/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Fast 易支付");
        data.put("description", "个人免签支付平台 API");
        data.put("version", "1.0.0");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("管理后台登录", "POST /api/admin/login");
        endpoints.put("管理后台控制台", "GET /api/admin/dashboard");
        endpoints.put("商户登录", "POST /api/merchant/login");
        endpoints.put("商户控制台", "GET /api/merchant/dashboard");
        endpoints.put("创建支付订单", "POST /api/pay/create");
        endpoints.put("查询订单", "GET /api/pay/query");
        data.put("endpoints", endpoints);
        
        return Result.success(data);
    }
}
