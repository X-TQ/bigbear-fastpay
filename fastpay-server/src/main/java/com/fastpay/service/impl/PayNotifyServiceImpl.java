package com.fastpay.service.impl;

import com.fastpay.common.Constants;
import com.fastpay.common.Result;
import com.fastpay.dto.BizCallbackDTO;
import com.fastpay.dto.PayNotifyDTO;
import com.fastpay.entity.Merchant;
import com.fastpay.entity.MerchantChannel;
import com.fastpay.entity.PayOrder;
import com.fastpay.mapper.MerchantChannelMapper;
import com.fastpay.mapper.MerchantMapper;
import com.fastpay.mapper.PayOrderMapper;
import com.fastpay.mapper.PayQrcodeMapper;
import com.fastpay.mapper.ShopMapper;
import com.fastpay.service.PayNotifyService;
import com.fastpay.service.PayOrderService;
import com.fastpay.websocket.PayResultWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 支付通知服务实现类
 * 处理监听软件推送的支付通知，自动匹配并确认订单支付
 *
 * @author FastPay
 */
@Slf4j
@Service
public class PayNotifyServiceImpl implements PayNotifyService {

    private final PayOrderMapper payOrderMapper;
    private final PayQrcodeMapper payQrcodeMapper;
    private final PayOrderService payOrderService;
    private final MerchantMapper merchantMapper;
    private final MerchantChannelMapper merchantChannelMapper;
    private final ShopMapper shopMapper;
    private final Executor wsNotifyExecutor;

    @Value("${fastpay.pay.order-timeout-minutes}")
    private Integer orderTimeoutMinutes;

    public PayNotifyServiceImpl(PayOrderMapper payOrderMapper,
                                PayQrcodeMapper payQrcodeMapper,
                                PayOrderService payOrderService,
                                MerchantMapper merchantMapper,
                                MerchantChannelMapper merchantChannelMapper,
                                ShopMapper shopMapper,
                                @Qualifier("wsNotifyExecutor") Executor wsNotifyExecutor) {
        this.payOrderMapper = payOrderMapper;
        this.payQrcodeMapper = payQrcodeMapper;
        this.payOrderService = payOrderService;
        this.merchantMapper = merchantMapper;
        this.merchantChannelMapper = merchantChannelMapper;
        this.shopMapper = shopMapper;
        this.wsNotifyExecutor = wsNotifyExecutor;
    }


    /**
     * 微信金额匹配正则表达式
     * 匹配格式如：微信支付收款0.01元
     */
    private static final Pattern WX_AMOUNT_PATTERN = Pattern.compile("微信支付收款([\\d.]+)元");

    /**
     * 支付宝金额匹配正则表达式
     * 匹配格式如：你已成功收款0.01元
     */
    private static final Pattern ALIPAY_AMOUNT_PATTERN = Pattern.compile("你已成功收款([\\d.]+)元");

    /**
     * 支付超时时间（秒）
     */
    private static final int PAYMENT_TIMEOUT = 300;

    /**
     * 时间戳允许的最大偏差（秒）
     */
    private static final int TIMESTAMP_MAX_DIFF = 120;

    @Override
    public boolean handleNotify(BizCallbackDTO  bizCallbackDTO) {
        final PayNotifyDTO notifyDTO = bizCallbackDTO.getPayNotifyDTO();
        final MerchantChannel merchantChannel = bizCallbackDTO.getMerchantChannel();

        // 支付类型：wxpay-微信，alipay-支付宝
        String type = merchantChannel.getPayType();
        if (!StringUtils.hasText(type)) {
            log.warn("支付类型为空");
            return false;
        }

        try {
            boolean success;
            if (Constants.PayType.WXPAY.equals(type)) {
                // 微信收款通知
                log.info("处理微信收款通知");
                success = handleWxPayNotify(bizCallbackDTO);
            } else if (Constants.PayType.ALIPAY.equals(type)) {
                // 支付宝收款通知
                log.info("处理支付宝收款通知");
                success = handleAlipayNotify(bizCallbackDTO);
            } else {
                log.warn("未知的支付类型: {}", type);
                return false;
            }

            if (success) {
                log.info("支付通知处理成功");
            } else {
                log.warn("支付通知处理失败，未找到匹配的订单");
            }
            return success;
        } catch (Exception e) {
            log.error("处理支付通知异常", e);
            return false;
        }
    }

    @Override
    public boolean handleWxPayNotify(BizCallbackDTO  bizCallbackDTO) {
        final PayNotifyDTO notifyDTO = bizCallbackDTO.getPayNotifyDTO();
        final String msg = notifyDTO.getMsg();
        final String receiveTime = notifyDTO.getReceiveTime();
        log.info("处理微信收款通知，msg={}, receiveTime={}", msg, receiveTime);

        // 解析收款金额
        BigDecimal amount = parseWxPayAmount(msg);
        if (amount == null) {
            log.warn("无法解析微信收款金额，msg={}", msg);
            return false;
        }
        log.info("解析到微信收款金额：{}", amount);

        return processPayment(amount, Constants.PayType.WXPAY, bizCallbackDTO);
    }

    @Override
    public boolean handleAlipayNotify(BizCallbackDTO  bizCallbackDTO) {
        final PayNotifyDTO notifyDTO = bizCallbackDTO.getPayNotifyDTO();
        final String title = notifyDTO.getTitle();
        final String msg = notifyDTO.getMsg();
        final String receiveTime = notifyDTO.getReceiveTime();
        log.info("处理支付宝收款通知，title={}, msg={}, receiveTime={}", title, msg, receiveTime);

        // 解析收款金额（从title中解析）
        BigDecimal amount = parseAlipayAmount(title);
        if (amount == null) {
            log.warn("无法解析支付宝收款金额，title={}", title);
            return false;
        }
        log.info("解析到支付宝收款金额：{}", amount);

        return processPayment(amount, Constants.PayType.ALIPAY, bizCallbackDTO);
    }

    /**
     * 处理支付（通用逻辑）
     */
    private boolean processPayment(BigDecimal amount, String payType, BizCallbackDTO bizCallbackDTO) {
        long startTime = System.currentTimeMillis();
        
        // 解析通知时间
        final PayNotifyDTO notifyDTO = bizCallbackDTO.getPayNotifyDTO();
        final String receiveTime = notifyDTO.getReceiveTime();
        LocalDateTime notifyTime = parseReceiveTime(receiveTime);

        // 查找匹配的订单
        PayOrder matchedOrder = findMatchingOrder(amount, payType, notifyTime, bizCallbackDTO);
        if (matchedOrder == null) {
            log.warn("未找到匹配的待支付订单，amount={}, payType={}, 耗时{}ms", 
                    amount, payType, System.currentTimeMillis() - startTime);
            return false;
        }

        log.info("找到匹配订单，orderNo={}, amount={}, payType={}, 查询耗时{}ms", 
                matchedOrder.getOrderNo(), amount, payType, System.currentTimeMillis() - startTime);

        // 保存订单信息用于异步通知
        final String orderNo = matchedOrder.getOrderNo();
        final String outTradeNo = matchedOrder.getOutTradeNo();
        final Long merchantId = matchedOrder.getMerchantId();
        final String amountStr = amount.toString();

        // 【优先】立即发送 WebSocket 通知，让用户页面第一时间收到支付结果
        wsNotifyExecutor.execute(() -> {
            try {
                notifyPaySuccess(outTradeNo, orderNo, amountStr);
            } catch (Exception e) {
                log.error("WebSocket通知发送异常，orderNo={}", orderNo, e);
            }
        });

        // 确认订单支付（包含数据库更新和商户回调）
        try {
            payOrderService.confirmPay(orderNo, merchantId);
            log.info("订单支付确认成功，orderNo={}, 总耗时{}ms", orderNo, System.currentTimeMillis() - startTime);
            return true;
        } catch (Exception e) {
            log.error("确认订单支付失败，orderNo={}", orderNo, e);
            return false;
        }
    }

    /**
     * 查找匹配的订单
     * 根据金额、支付类型、商户ID查找待支付订单
     */
    private PayOrder findMatchingOrder(BigDecimal amount, String payType, LocalDateTime notifyTime, BizCallbackDTO bizCallbackDTO) {
        final MerchantChannel merchantChannel = bizCallbackDTO.getMerchantChannel();
        final Long merchantId = merchantChannel.getMerchantId();
        final Long channelId = merchantChannel.getId();

        
        // 查询该商户该通道下的待支付订单
        // 条件：金额匹配、支付类型匹配、状态为待支付、未过期
        List<PayOrder> pendingOrders = payOrderMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PayOrder>()
                        .eq(PayOrder::getMerchantId, merchantId)
                        .eq(PayOrder::getPayType, payType)
                        .eq(PayOrder::getStatus, Constants.OrderStatus.UNPAID)
                        .eq(PayOrder::getAmount, amount)
                        // 未过期
                        .gt(PayOrder::getExpireTime, LocalDateTime.now())
                        // 按创建时间升序，优先匹配较早的订单
                        .orderByAsc(PayOrder::getCreateTime)
        );
        
        if (pendingOrders == null || pendingOrders.isEmpty()) {
            log.info("未找到匹配订单，merchantId={}, channelId={}, amount={}, payType={}", 
                    merchantId, channelId, amount, payType);
            return null;
        }
        
        // 如果有多个匹配订单，优先选择时间最接近的
        for (PayOrder order : pendingOrders) {
            if (isTimeValid(order, notifyTime)) {
                log.info("找到匹配订单，orderNo={}, amount={}", order.getOrderNo(), amount);
                return order;
            }
        }
        
        // 如果没有时间匹配的，返回null
        return null;
    }

    /**
     * 验证订单时间是否有效
     */
    private boolean isTimeValid(PayOrder order, LocalDateTime notifyTime) {
        LocalDateTime createTime = order.getCreateTime();
        if (createTime == null) {
            return true;  // 如果没有创建时间，默认有效
        }

        LocalDateTime checkTime = notifyTime != null ? notifyTime : LocalDateTime.now();

        // 订单创建时间应该在通知时间之前，且间隔不超过5分钟
        return createTime.isBefore(checkTime) && createTime.plusMinutes(orderTimeoutMinutes).isAfter(checkTime);
    }

    /**
     * 解析接收时间
     */
    private LocalDateTime parseReceiveTime(String receiveTime) {
        if (!StringUtils.hasText(receiveTime)) {
            return null;
        }
        try {
            return LocalDateTime.parse(receiveTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            log.warn("解析通知时间失败，receiveTime={}", receiveTime);
            return null;
        }
    }


    @Override
    public BigDecimal parseWxPayAmount(String msg) {
        if (!StringUtils.hasText(msg)) {
            return null;
        }

        Matcher matcher = WX_AMOUNT_PATTERN.matcher(msg);
        if (matcher.find()) {
            try {
                return new BigDecimal(matcher.group(1));
            } catch (NumberFormatException e) {
                log.error("微信金额格式转换失败，amountStr={}", matcher.group(1), e);
            }
        }
        return null;
    }

    @Override
    public BigDecimal parseAlipayAmount(String title) {
        if (!StringUtils.hasText(title)) {
            return null;
        }

        Matcher matcher = ALIPAY_AMOUNT_PATTERN.matcher(title);
        if (matcher.find()) {
            try {
                return new BigDecimal(matcher.group(1));
            } catch (NumberFormatException e) {
                log.error("支付宝金额格式转换失败，amountStr={}", matcher.group(1), e);
            }
        }
        return null;
    }

    @Override
    public boolean verifySign(String timestamp, String sign, String secret) {
        if (!StringUtils.hasText(timestamp) || !StringUtils.hasText(sign) || !StringUtils.hasText(secret)) {
            return false;
        }

        // 验证时间戳是否在允许范围内
        try {
            long ts = Long.parseLong(timestamp);
            if (!areTimestampsWithinLimit(ts, System.currentTimeMillis(), TIMESTAMP_MAX_DIFF)) {
                log.warn("时间戳超出允许范围，timestamp={}", timestamp);
                return false;
            }
        } catch (NumberFormatException e) {
            log.warn("时间戳格式错误，timestamp={}", timestamp);
            return false;
        }

        // 验证签名
        String expectedSign = generateSign(timestamp, secret);
        return expectedSign.equals(sign);
    }

    @Override
    public String generateSign(String timestamp, String secret) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(Base64.getEncoder().encodeToString(signData), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("生成签名失败", e);
            return "";
        }
    }

    @Override
    public Result<Void> callback(PayNotifyDTO notifyDTO) {
        log.info("收到支付回调通知，channelId={}, title={}, msg={}", 
                notifyDTO.getChannelId(), notifyDTO.getTitle(), notifyDTO.getMsg());
        
        // 参数校验
        final Long channelId = notifyDTO.getChannelId();
        if (channelId == null) {
            log.warn("通道ID为空");
            return Result.error("通道ID不能为空");
        }
        
        // 查询商户通道信息
        final MerchantChannel merchantChannel = merchantChannelMapper.selectById(channelId);
        if (merchantChannel == null) {
            log.warn("通道不存在，channelId={}", channelId);
            return Result.error("通道不存在");
        }
        
        // 检查通道状态
        if (merchantChannel.getStatus() != 1) {
            log.warn("通道已禁用，channelId={}", channelId);
            return Result.error("通道已禁用");
        }
        
        final Long merchantId = merchantChannel.getMerchantId();
        final Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            log.warn("商户不存在，merchantId={}", merchantId);
            return Result.error("商户不存在");
        }
        
        // 检查商户状态
        if (merchant.getStatus() != 1) {
            log.warn("商户已禁用，merchantId={}", merchantId);
            return Result.error("商户已禁用");
        }

        // 验证签名
        if (!verifySign(notifyDTO.getTimestamp(), notifyDTO.getSign(), merchant.getApiSecret())) {
            log.warn("签名验证失败，channelId={}, timestamp={}", channelId, notifyDTO.getTimestamp());
            return Result.error("签名验证失败");
        }
        
        log.info("签名验证通过，开始处理支付通知");

        BizCallbackDTO bizCallbackDTO = new BizCallbackDTO();
        bizCallbackDTO.setPayNotifyDTO(notifyDTO);
        bizCallbackDTO.setMerchant(merchant);
        bizCallbackDTO.setMerchantChannel(merchantChannel);

        // 处理支付通知
        boolean success = handleNotify(bizCallbackDTO);
        if (success) {
            log.info("支付通知处理成功，channelId={}", channelId);
            return Result.success("处理成功", null);
        } else {
            // 即使未找到匹配订单，也返回成功，避免监听软件重试
            log.info("支付通知已接收但未匹配到订单，channelId={}", channelId);
            return Result.success("已接收", null);
        }
    }

    /**
     * 校验两个时间戳是否在允许的时间差内
     */
    private boolean areTimestampsWithinLimit(long timestamp1Ms, long timestamp2Ms, long maxDifferenceSeconds) {
        Instant instant1 = Instant.ofEpochMilli(timestamp1Ms);
        Instant instant2 = Instant.ofEpochMilli(timestamp2Ms);
        Duration duration = Duration.between(instant1, instant2);
        return Math.abs(duration.getSeconds()) <= maxDifferenceSeconds;
    }

    /**
     * 发送支付成功 WebSocket 通知
     * 用于通知支付页面订单已支付成功，前端收到后跳转到支付成功页面
     *
     * @param outTradeNo 商户订单号
     * @param orderNo    平台订单号
     * @param payAmount  实付金额
     */
    private void notifyPaySuccess(String outTradeNo, String orderNo, String payAmount) {
        try {
            if (PayResultWebSocket.hasActiveConnection(outTradeNo)) {
                PayResultWebSocket.sendPaySuccess(outTradeNo, orderNo, payAmount);
                log.info("已发送支付成功WebSocket通知，outTradeNo={}, orderNo={}, payAmount={}", outTradeNo, orderNo, payAmount);
            } else {
                log.info("无活跃WebSocket连接，跳过通知，outTradeNo={}", outTradeNo);
            }
        } catch (Exception e) {
            log.error("发送支付成功WebSocket通知异常，outTradeNo={}", outTradeNo, e);
        }
    }
}
