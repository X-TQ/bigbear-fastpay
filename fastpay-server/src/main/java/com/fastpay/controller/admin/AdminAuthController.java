package com.fastpay.controller.admin;

import com.fastpay.common.Result;
import com.fastpay.dto.LoginDTO;
import com.fastpay.service.AdminService;
import com.fastpay.vo.DashboardVO;
import com.fastpay.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台 - 认证控制器
 * 处理管理员登录、控制台数据等
 *
 * @author FastPay
 */
@Tag(name = "管理后台-认证", description = "管理员登录、控制台数据等接口")
@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AdminService adminService;

    public AdminAuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 管理员登录
     *
     * @param dto     登录信息
     * @param request HTTP请求
     * @return 登录结果
     */
    @Operation(summary = "管理员登录", description = "管理员账号密码登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);
        LoginVO vo = adminService.login(dto, ip);
        return Result.success("登录成功", vo);
    }

    /**
     * 获取控制台统计数据
     *
     * @return 统计数据
     */
    @Operation(summary = "控制台数据", description = "获取控制台统计数据")
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboard() {
        DashboardVO vo = adminService.getDashboard();
        return Result.success(vo);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
