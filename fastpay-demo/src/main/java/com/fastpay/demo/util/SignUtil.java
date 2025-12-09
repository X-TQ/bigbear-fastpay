package com.fastpay.demo.util;

import cn.hutool.crypto.SecureUtil;

import java.util.Map;
import java.util.TreeMap;

/**
 * 签名工具类
 */
public class SignUtil {

    /**
     * 生成签名
     *
     * @param params 参数 Map
     * @param apiKey API 密钥
     * @return 签名字符串
     */
    public static String generateSign(Map<String, Object> params, String apiKey) {
        // 1. 按字母顺序排序
        TreeMap<String, Object> sortedParams = new TreeMap<>(params);

        // 2. 拼接参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // 跳过空值和 sign 参数
            if (value != null && !"".equals(value.toString()) && !"sign".equals(key)) {
                sb.append(key).append("=").append(value).append("&");
            }
        }

        // 3. 拼接密钥
        sb.append("key=").append(apiKey);

        // 4. MD5 加密并转大写
        return SecureUtil.md5(sb.toString()).toUpperCase();
    }

    /**
     * 验证签名
     *
     * @param params 参数 Map（包含 sign）
     * @param apiKey API 密钥
     * @return 是否验证通过
     */
    public static boolean verifySign(Map<String, Object> params, String apiKey) {
        String sign = (String) params.get("sign");
        if (sign == null || sign.isEmpty()) {
            return false;
        }
        String calculatedSign = generateSign(params, apiKey);
        return sign.equals(calculatedSign);
    }
}
