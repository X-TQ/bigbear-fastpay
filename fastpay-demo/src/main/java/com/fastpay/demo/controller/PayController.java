package com.fastpay.demo.controller;

import cn.hutool.json.JSONObject;
import com.fastpay.demo.service.FastPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class PayController {

    private final FastPayService fastPayService;

    /**
     * 首页
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 页面跳转支付 - 演示页面
     */
    @GetMapping("/page-pay")
    public String pagePay() {
        return "page_pay";
    }

    /**
     * 页面跳转支付 - 提交
     */
    @PostMapping("/page-pay/submit")
    public String pagePaySubmit(
            @RequestParam String amount,
            @RequestParam String subject,
            @RequestParam String payType,
            Model model) {

        // 生成商户订单号
        String outTradeNo = "DEMO" + System.currentTimeMillis();

        // 构建支付参数
        Map<String, Object> params = fastPayService.buildPagePayParams(outTradeNo, amount, subject, payType);

        model.addAttribute("submitUrl", fastPayService.getSubmitUrl());
        model.addAttribute("params", params);

        return "page_pay_submit";
    }

    /**
     * API 接口支付 - 演示页面
     */
    @GetMapping("/api-pay")
    public String apiPay() {
        return "api_pay";
    }

    /**
     * API 接口支付 - 创建订单
     */
    @PostMapping("/api-pay/create")
    @ResponseBody
    public Map<String, Object> apiPayCreate(
            @RequestParam String amount,
            @RequestParam String subject,
            @RequestParam String payType) {

        Map<String, Object> result = new HashMap<>();

        // 生成商户订单号
        String outTradeNo = "DEMO" + System.currentTimeMillis();

        // 调用 API 创建订单
        JSONObject response = fastPayService.createOrder(outTradeNo, amount, subject, payType);

        if (response.getInt("code") == 200) {
            result.put("success", true);
            // 提取需要的字段，避免 JSONNull 序列化问题
            JSONObject data = response.getJSONObject("data");
            if (data != null) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("orderNo", data.getStr("orderNo"));
                dataMap.put("payPageUrl", data.getStr("payPageUrl"));
                dataMap.put("qrcodeUrl", data.getStr("qrcodeUrl"));
                dataMap.put("amount", amount);  // 返回订单金额
                result.put("data", dataMap);
            }
            result.put("outTradeNo", outTradeNo);
        } else {
            result.put("success", false);
            result.put("message", response.getStr("message"));
        }

        return result;
    }

    /**
     * 查询订单状态
     */
    @GetMapping("/api-pay/query")
    @ResponseBody
    public Map<String, Object> queryOrder(@RequestParam String outTradeNo) {
        Map<String, Object> result = new HashMap<>();

        JSONObject response = fastPayService.queryOrder(outTradeNo);

        if (response.getInt("code") == 200) {
            result.put("success", true);
            // 提取需要的字段，避免 JSONNull 序列化问题
            JSONObject data = response.getJSONObject("data");
            if (data != null) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("orderNo", data.getStr("orderNo"));
                dataMap.put("outTradeNo", data.getStr("outTradeNo"));
                dataMap.put("amount", data.getStr("amount"));
                dataMap.put("payAmount", data.getStr("payAmount"));
                dataMap.put("status", data.getInt("status"));
                dataMap.put("payTime", data.getStr("payTime"));
                result.put("data", dataMap);
            }
        } else {
            result.put("success", false);
            result.put("message", response.getStr("message"));
        }

        return result;
    }

    /**
     * 支付成功同步跳转
     */
    @GetMapping("/pay/return")
    public String payReturn(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String outTradeNo,
            @RequestParam(required = false) Integer status,
            Model model) {

        model.addAttribute("orderNo", orderNo);
        model.addAttribute("outTradeNo", outTradeNo);
        model.addAttribute("status", status);
        model.addAttribute("success", status != null && status == 1);

        return "result";
    }

    /**
     * 异步通知回调
     */
    @PostMapping("/pay/notify")
    @ResponseBody
    public String payNotify(@RequestParam Map<String, Object> params) {
        log.info("收到支付回调通知: {}", params);

        // 验证签名
        if (!fastPayService.verifyNotify(params)) {
            log.error("签名验证失败");
            return "fail";
        }

        // 获取订单信息
        String orderNo = (String) params.get("orderNo");
        String outTradeNo = (String) params.get("outTradeNo");
        String amount = (String) params.get("amount");
        Integer status = Integer.parseInt(params.get("status").toString());

        log.info("订单支付成功 - 平台订单号: {}, 商户订单号: {}, 金额: {}", orderNo, outTradeNo, amount);

        // TODO: 在这里处理您的业务逻辑
        // 1. 验证订单是否存在
        // 2. 验证订单金额是否一致
        // 3. 更新订单状态
        // 4. 发货或开通服务

        // 返回 success 表示处理成功
        return "success";
    }
}
