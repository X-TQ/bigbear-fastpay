package com.fastpay.controller.pay;

import com.fastpay.common.Constants;
import com.fastpay.common.Result;
import com.fastpay.dto.CreateOrderDTO;
import com.fastpay.entity.Merchant;
import com.fastpay.entity.PayOrder;
import com.fastpay.entity.PayQrcode;
import com.fastpay.entity.Shop;
import com.fastpay.mapper.MerchantMapper;
import com.fastpay.mapper.PayQrcodeMapper;
import com.fastpay.mapper.ShopMapper;
import com.fastpay.service.PayOrderService;
import com.fastpay.vo.PayResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付接口控制器
 * 提供支付订单创建、查询、支付页面等功能
 *
 * @author FastPay
 */
@Slf4j
@Tag(name = "支付接口", description = "支付订单创建、查询、支付页面等公开接口")
@RestController
@RequestMapping("/api/pay")
public class PayController {

    private final PayOrderService payOrderService;
    private final MerchantMapper merchantMapper;
    private final PayQrcodeMapper payQrcodeMapper;
    private final ShopMapper shopMapper;

    @Value("${fastpay.pay.page-domain}")
    private String pageDomain;

    public PayController(PayOrderService payOrderService, MerchantMapper merchantMapper,
                         PayQrcodeMapper payQrcodeMapper, ShopMapper shopMapper) {
        this.payOrderService = payOrderService;
        this.merchantMapper = merchantMapper;
        this.payQrcodeMapper = payQrcodeMapper;
        this.shopMapper = shopMapper;
    }

    /**
     * 创建支付订单（API接口）
     */
    @Operation(summary = "创建订单", description = "创建支付订单")
    @PostMapping("/create")
    @ResponseBody
    public Result<PayResultVO> createOrder(@Valid @RequestBody CreateOrderDTO dto, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        // 强制设置为api方式
        dto.setPayMethod(Constants.PayMethod.API);
        PayResultVO vo = payOrderService.createOrder(dto, clientIp);
        return Result.success(vo);
    }

    /**
     * 页面跳转支付接口
     * 商户通过此接口创建订单并直接跳转到支付页面
     * 
     * 请求方式：POST
     */
    @Operation(summary = "页面跳转支付", description = "创建订单并跳转到支付页面")
    @PostMapping(value = "/submit")
    public void submitPay(@Valid CreateOrderDTO dto, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws IOException {
        log.info("页面跳转支付请求: merchantNo={}, outTradeNo={}, amount={}, payType={}", 
                dto.getMerchantNo(), dto.getOutTradeNo(), dto.getAmount(), dto.getPayType());
        
        try {
            // 强制设置为页面跳转方式
            dto.setPayMethod(Constants.PayMethod.PAGE);
            
            // 创建订单
            String clientIp = getClientIp(request);
            PayResultVO vo = payOrderService.createOrder(dto, clientIp);
            final String returnUrl = dto.getReturnUrl();
            // 如果商户传了 returnUrl，更新订单的 returnUrl
            if (returnUrl != null && !returnUrl.isEmpty()) {
                payOrderService.updateReturnUrl(vo.getOrderNo(), returnUrl);
            }
            
            log.info("页面跳转支付订单创建成功: orderNo={}, payPageUrl={}", vo.getOrderNo(), vo.getPayPageUrl());

            // 重定向到支付页面
            response.sendRedirect(vo.getPayPageUrl());
        } catch (Exception e) {
            log.error("页面跳转支付失败: merchantNo={}, outTradeNo={}", dto.getMerchantNo(), dto.getOutTradeNo(), e);
            // 跳转到错误页面
            response.sendRedirect(pageDomain + "/pay/error?message=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    /**
     * 查询订单状态
     */
    @Operation(summary = "查询订单", description = "查询订单状态")
    @GetMapping("/query")
    @ResponseBody
    public Result<Map<String, Object>> queryOrder(
            @RequestParam String merchantNo,
            @RequestParam String outTradeNo) {
        PayOrder order = payOrderService.queryOrderByOutTradeNo(merchantNo, outTradeNo);
        if (order == null) {
            return Result.error("订单不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("outTradeNo", order.getOutTradeNo());
        result.put("amount", order.getAmount());
        result.put("payAmount", order.getPayAmount());
        result.put("status", order.getStatus());
        result.put("payTime", order.getPayTime());
        result.put("expireTime", order.getExpireTime());
        return Result.success(result);
    }

    /**
     * 获取支付页面数据
     * 前端通过此接口获取订单和二维码信息，然后渲染支付页面
     */
    @Operation(summary = "支付页面数据", description = "获取支付页面数据，包含订单信息和收款二维码")
    @GetMapping("/page/{orderNo}")
    public Result<Map<String, Object>> getPayPageData(@PathVariable String orderNo) {
        PayOrder order = payOrderService.getPayPageData(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }

        PayQrcode qrcode = payQrcodeMapper.selectById(order.getQrcodeId());
        
        // 获取店铺名称：优先从订单的shopId获取，其次从二维码的shopId获取
        String shopName = "收款方";
        Long shopId = order.getShopId();
        if (shopId == null && qrcode != null) {
            shopId = qrcode.getShopId();
        }
        if (shopId != null) {
            Shop shop = shopMapper.selectById(shopId);
            if (shop != null && shop.getShopName() != null) {
                shopName = shop.getShopName();
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("merchantNo", merchantMapper.selectById(order.getMerchantId()).getMerchantNo());
        result.put("orderNo", order.getOrderNo());
        result.put("outTradeNo", order.getOutTradeNo());
        result.put("amount", order.getAmount());
        result.put("subject", order.getSubject());
        result.put("status", order.getStatus());
        result.put("payType", order.getPayType());
        result.put("expireTime", order.getExpireTime());
        result.put("shopName", shopName);
        result.put("qrcodeUrl", qrcode != null ? qrcode.getQrcodeUrl() : "");
        result.put("returnUrl", order.getReturnUrl());

        return Result.success(result);
    }

    /**
     * 查询订单支付状态（轮询用）
     */
    @Operation(summary = "查询支付状态", description = "轮询查询订单支付状态")
    @GetMapping("/status/{orderNo}")
    public Result<Map<String, Object>> getPayStatus(@PathVariable String orderNo) {
        PayOrder order = payOrderService.queryOrder(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("status", order.getStatus());
        result.put("payTime", order.getPayTime());
        result.put("payAmount", order.getPayAmount());

        return Result.success(result);
    }

    /**
     * 获取支付结果数据
     */
    @Operation(summary = "支付结果", description = "获取支付结果数据")
    @GetMapping("/result/{orderNo}")
    public Result<Map<String, Object>> getPayResult(@PathVariable String orderNo) {
        PayOrder order = payOrderService.queryOrder(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }

        Merchant merchant = merchantMapper.selectById(order.getMerchantId());

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("outTradeNo", order.getOutTradeNo());
        result.put("amount", order.getAmount());
        result.put("payAmount", order.getPayAmount());
        result.put("subject", order.getSubject());
        result.put("status", order.getStatus());
        result.put("payType", order.getPayType());
        result.put("payTime", order.getPayTime());
        result.put("merchantName", merchant != null ? merchant.getMerchantName() : "");
        result.put("returnUrl", order.getReturnUrl());

        return Result.success(result);
    }

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
