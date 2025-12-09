package com.fastpay.config;

import com.fastpay.common.BusinessException;
import com.fastpay.common.Constants;
import com.fastpay.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 * 验证请求的 JWT Token
 *
 * @author FastPay
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取 Token
        String authHeader = request.getHeader(Constants.Header.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(Constants.Header.BEARER)) {
            throw BusinessException.unauthorized("请先登录");
        }

        String token = authHeader.substring(Constants.Header.BEARER.length());

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            throw BusinessException.unauthorized("登录已过期，请重新登录");
        }

        // 获取用户信息并存入请求属性
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        String userType = jwtUtil.getUserType(token);

        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("userType", userType);

        // 验证访问权限
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/admin/") && !"admin".equals(userType)) {
            throw BusinessException.forbidden("无权访问管理后台");
        }
        if (uri.startsWith("/api/merchant/") && !"merchant".equals(userType)) {
            throw BusinessException.forbidden("无权访问商户平台");
        }

        return true;
    }
}
