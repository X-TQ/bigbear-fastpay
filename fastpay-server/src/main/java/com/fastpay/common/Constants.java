package com.fastpay.common;

/**
 * 系统常量定义
 * 集中管理系统中使用的各种常量值
 *
 * @author FastPay
 */
public class Constants {

    /**
     * 用户类型
     */
    public static class UserType {
        /** 管理员 */
        public static final Integer ADMIN = 1;
        /** 商户 */
        public static final Integer MERCHANT = 2;
    }

    /**
     * 状态常量
     */
    public static class Status {
        /** 禁用 */
        public static final Integer DISABLED = 0;
        /** 启用 */
        public static final Integer ENABLED = 1;
        /** 待审核 */
        public static final Integer PENDING = 2;
    }

    /**
     * 支付类型
     */
    public static class PayType {
        /** 微信支付 */
        public static final String WXPAY = "wxpay";
        /** 支付宝 */
        public static final String ALIPAY = "alipay";
    }

    /**
     * 监听模式
     */
    public static class ListenMode {
        /** 手动确认 */
        public static final Integer MANUAL = 1;
        /** 自动监听 */
        public static final Integer AUTO = 2;
    }

    /**
     * 订单状态
     */
    public static class OrderStatus {
        /** 待支付 */
        public static final Integer UNPAID = 0;
        /** 已支付 */
        public static final Integer PAID = 1;
        /** 已过期 */
        public static final Integer EXPIRED = 2;
        /** 已关闭 */
        public static final Integer CLOSED = 3;
    }

    /**
     * 支付方式
     */
    public static class PayMethod {
        /** 页面跳转支付 */
        public static final String PAGE = "page";
        /** API接口支付 */
        public static final String API = "api";
    }

    /**
     * Token 前缀
     */
    public static class TokenPrefix {
        /** 管理员Token前缀 */
        public static final String ADMIN = "admin:";
        /** 商户Token前缀 */
        public static final String MERCHANT = "merchant:";
    }

    /**
     * 请求头
     */
    public static class Header {
        /** Authorization 请求头 */
        public static final String AUTHORIZATION = "Authorization";
        /** Bearer 前缀 */
        public static final String BEARER = "Bearer ";
    }
}
