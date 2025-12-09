package com.fastpay.util;

import cn.hutool.crypto.SecureUtil;

import java.util.*;

/**
 * 签名工具类
 * 用于API接口的签名生成和验证
 *
 * @author FastPay
 */
public class SignUtil {

    /**
     * 生成签名
     * 签名规则：将所有非空参数按字母顺序排序，拼接成 key=value&key=value 格式，
     * 最后拼接 &key=apiSecret，然后进行 MD5 加密并转大写
     *
     * @param params    参数Map
     * @param apiSecret API密钥
     * @return 签名字符串
     */
    public static String generateSign(Map<String, Object> params, String apiSecret) {
        // 过滤空值和sign参数
        Map<String, String> filteredParams = new TreeMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null && !"sign".equals(key)) {
                filteredParams.put(key, value+"");
            }
        }

        // 拼接参数
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : filteredParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 拼接密钥
        sb.append("&key=").append(apiSecret);

        // MD5加密并转大写
        return SecureUtil.md5(sb.toString()).toUpperCase();
    }

    /**
     * 验证签名
     *
     * @param params    参数Map（包含sign参数）
     * @param apiSecret API密钥
     * @return 签名是否正确
     */
    public static boolean verifySign(Map<String, Object> params, String apiSecret) {
        Object signObj = params.get("sign");
        if (signObj == null) {
            return false;
        }
        String sign = signObj.toString();
        if (sign.isEmpty() || "null".equals(sign)) {
            return false;
        }

        String calculatedSign = generateSign(params, apiSecret);
        return sign.equalsIgnoreCase(calculatedSign);
    }

    /**
     * 生成随机字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String generateNonceStr(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 生成订单号
     * 格式：FP + 年月日时分秒 + 6位随机数
     *
     * @return 订单号
     */
    public static String generateOrderNo() {
        String timestamp = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", new Date());
        String random = String.format("%06d", new Random().nextInt(1000000));
        return "FP" + timestamp + random;
    }

    /**
     * 生成商户编号
     * 格式：M + 时间戳后8位 + 4位随机数
     *
     * @return 商户编号
     */
    public static String generateMerchantNo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.format("%04d", new Random().nextInt(10000));
        return "M" + timestamp.substring(timestamp.length() - 8) + random;
    }

    /**
     * 生成店铺编号
     * 格式：S + 时间戳后8位 + 4位随机数
     *
     * @return 店铺编号
     */
    public static String generateShopNo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.format("%04d", new Random().nextInt(10000));
        return "S" + timestamp.substring(timestamp.length() - 8) + random;
    }

    /**
     * 生成API密钥
     *
     * @return API密钥（32位）
     */
    public static String generateApiKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成API密钥Secret
     *
     * @return API密钥Secret（32位）
     */
    public static String generateApiSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
