package com.fastpay.demo.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fastpay.demo.config.FastPayConfig;
import com.fastpay.demo.util.SignUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * FAST 易支付服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FastPayService {

    private final FastPayConfig config;

    /**
     * 构建页面跳转支付参数
     *
     * @param outTradeNo 商户订单号
     * @param amount     金额
     * @param subject    商品名称
     * @param payType    支付类型 wxpay/alipay
     * @return 支付参数 Map
     */
    public Map<String, Object> buildPagePayParams(String outTradeNo, String amount, String subject, String payType) {
        Map<String, Object> params = new HashMap<>();
        params.put("merchantNo", config.getMerchantNo());
        params.put("outTradeNo", outTradeNo);
        params.put("shopNo", config.getShopNo());
        params.put("amount", amount);
        params.put("subject", subject);
        params.put("payType", payType);
        params.put("timestamp", System.currentTimeMillis() / 1000);
        params.put("returnUrl", config.getReturnUrl());

        // 生成签名
        String sign = SignUtil.generateSign(params, config.getApiSecret());
        params.put("sign", sign);
        return params;
    }

    /**
     * API 接口创建订单
     *
     * @param outTradeNo 商户订单号
     * @param amount     金额
     * @param subject    商品名称
     * @param payType    支付类型 wxpay/alipay
     * @return 创建结果
     */
    public JSONObject createOrder(String outTradeNo, String amount, String subject, String payType) {
        Map<String, Object> params = new HashMap<>();
        params.put("merchantNo", config.getMerchantNo());
        params.put("outTradeNo", outTradeNo);
        params.put("shopNo", config.getShopNo());
        params.put("amount", amount);
        params.put("subject", subject);
        params.put("payType", payType);
        params.put("timestamp", System.currentTimeMillis() / 1000);

        // 生成签名
        String sign = SignUtil.generateSign(params, config.getApiSecret());
        params.put("sign", sign);

        log.info("API 创建订单请求参数: {}", params);

        try {
            String url = config.getCreateUrl();
            log.info("API 创建订单请求 URL: {}", url);
            
            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(params))
                    .timeout(10000)
                    .execute();

            String body = response.body();
            log.info("API 创建订单响应状态码: {}, 响应内容: {}", response.getStatus(), body);

            return JSONUtil.parseObj(body);
        } catch (Exception e) {
            log.error("API 创建订单失败", e);
            JSONObject error = new JSONObject();
            error.set("code", -1);
            error.set("message", "请求失败: " + e.getMessage());
            return error;
        }
    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号
     * @return 查询结果
     */
    public JSONObject queryOrder(String outTradeNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("merchantNo", config.getMerchantNo());
        params.put("outTradeNo", outTradeNo);
        params.put("timestamp", System.currentTimeMillis() / 1000);

        // 生成签名
        String sign = SignUtil.generateSign(params, config.getApiSecret());
        params.put("sign", sign);

        log.info("查询订单请求参数: {}", params);

        try {
            // 服务端查询接口只需要 merchantNo 和 outTradeNo

            StringBuilder url = new StringBuilder(config.getQueryUrl());
            url.append("?merchantNo=").append(params.get("merchantNo"));
            url.append("&outTradeNo=").append(params.get("outTradeNo"));

            log.info("查询订单请求 URL: {}", url);

            HttpResponse response = HttpRequest.get(url.toString())
                    .timeout(10000)
                    .execute();

            String body = response.body();
            log.info("查询订单响应状态码: {}, 响应内容: {}", response.getStatus(), body);

            return JSONUtil.parseObj(body);
        } catch (Exception e) {
            log.error("查询订单失败", e);
            JSONObject error = new JSONObject();
            error.set("code", -1);
            error.set("message", "请求失败: " + e.getMessage());
            return error;
        }
    }

    /**
     * 验证回调签名
     *
     * @param params 回调参数
     * @return 是否验证通过
     */
    public boolean verifyNotify(Map<String, Object> params) {
        return SignUtil.verifySign(params, config.getApiSecret());
    }

    /**
     * 获取支付网关提交地址
     */
    public String getSubmitUrl() {
        return config.getSubmitUrl();
    }
}
