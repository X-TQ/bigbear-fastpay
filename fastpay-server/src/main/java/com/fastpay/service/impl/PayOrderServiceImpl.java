package com.fastpay.service.impl;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fastpay.common.BusinessException;
import com.fastpay.common.Constants;
import com.fastpay.dto.CreateOrderDTO;
import com.fastpay.entity.Merchant;
import com.fastpay.entity.PayOrder;
import com.fastpay.entity.PayQrcode;
import com.fastpay.entity.Shop;
import com.fastpay.mapper.MerchantMapper;
import com.fastpay.mapper.PayOrderMapper;
import com.fastpay.mapper.PayQrcodeMapper;
import com.fastpay.mapper.ShopMapper;
import com.fastpay.service.PayOrderService;
import com.fastpay.service.PayQrcodeService;
import com.fastpay.util.SignUtil;
import com.fastpay.vo.PayResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.Executor;

/**
 * 支付订单服务实现类
 *
 * @author FastPay
 */
@Slf4j
@Service
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements PayOrderService {

    private final MerchantMapper merchantMapper;
    private final ShopMapper shopMapper;
    private final PayQrcodeMapper payQrcodeMapper;
    private final PayQrcodeService payQrcodeService;
    private final Executor payNotifyExecutor;

    @Value("${fastpay.pay.order-timeout-minutes}")
    private Integer orderTimeoutMinutes;

    @Value("${fastpay.pay.page-domain}")
    private String pageDomain;

    public PayOrderServiceImpl(MerchantMapper merchantMapper, ShopMapper shopMapper,
                               PayQrcodeMapper payQrcodeMapper, PayQrcodeService payQrcodeService,
                               @Qualifier("payNotifyExecutor") Executor payNotifyExecutor) {
        this.merchantMapper = merchantMapper;
        this.shopMapper = shopMapper;
        this.payQrcodeMapper = payQrcodeMapper;
        this.payQrcodeService = payQrcodeService;
        this.payNotifyExecutor = payNotifyExecutor;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayResultVO createOrder(CreateOrderDTO dto, String clientIp) {
        // 验证商户
        Merchant merchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMerchantNo, dto.getMerchantNo()));
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        if (!Constants.Status.ENABLED.equals(merchant.getStatus())) {
            throw new BusinessException("商户已被禁用");
        }

        // 验证签名
        TreeMap<String, Object> signParams = new TreeMap<>();
        signParams.put("merchantNo", dto.getMerchantNo());
        signParams.put("outTradeNo", dto.getOutTradeNo());
        signParams.put("payType", dto.getPayType());
        signParams.put("amount", dto.getAmount().toPlainString());
        signParams.put("subject", dto.getSubject());
        signParams.put("timestamp", String.valueOf(dto.getTimestamp()));
        if (StringUtils.hasText(dto.getReturnUrl())) {
            signParams.put("returnUrl", dto.getReturnUrl());
        }
        if (StringUtils.hasText(dto.getExtParam())) {
            signParams.put("extParam", dto.getExtParam());
        }
        signParams.put("sign", dto.getSign());

        if (!SignUtil.verifySign(signParams, merchant.getApiSecret())) {
            throw new BusinessException("签名验证失败");
        }

        // 验证时间戳（5分钟内有效）
        long currentTime = System.currentTimeMillis() / 1000;
        if (Math.abs(currentTime - dto.getTimestamp()) > 300) {
            throw new BusinessException("请求已过期");
        }

        // 检查订单号是否重复
        Long existCount = this.count(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getMerchantId, merchant.getId())
                .eq(PayOrder::getOutTradeNo, dto.getOutTradeNo()));
        if (existCount > 0) {
            throw new BusinessException("商户订单号已存在");
        }

        // 获取可用的收款二维码
        PayQrcode qrcode = payQrcodeService.getAvailableQrcode(merchant.getId(), dto.getPayType());
        if (qrcode == null) {
            throw new BusinessException("暂无可用的收款通道，请联系商户");
        }

        // 获取店铺信息
        Shop shop = shopMapper.selectById(qrcode.getShopId());

        // 创建订单
        PayOrder order = new PayOrder();
        order.setOrderNo(SignUtil.generateOrderNo());
        order.setOutTradeNo(dto.getOutTradeNo());
        order.setMerchantId(merchant.getId());
        order.setShopId(qrcode.getShopId());
        order.setQrcodeId(qrcode.getId());
        order.setPayType(qrcode.getPayType());
        order.setPayMethod(StringUtils.hasText(dto.getPayMethod()) ? dto.getPayMethod() : Constants.PayMethod.PAGE);
        order.setAmount(dto.getAmount());
        order.setSubject(dto.getSubject());
        order.setStatus(Constants.OrderStatus.UNPAID);
        order.setNotifyUrl(merchant.getNotifyUrl());
        order.setReturnUrl(merchant.getReturnUrl());
        order.setNotifyStatus(0);
        order.setNotifyCount(0);
        order.setExpireTime(LocalDateTime.now().plusMinutes(orderTimeoutMinutes));
        order.setClientIp(clientIp);
        order.setExtParam(dto.getExtParam());

        this.save(order);
        log.info("创建支付订单成功: orderNo={}, amount={}", order.getOrderNo(), order.getAmount());

        // 构建返回结果
        PayResultVO vo = new PayResultVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setOutTradeNo(order.getOutTradeNo());
        vo.setAmount(order.getAmount());
        vo.setPayType(order.getPayType());
        vo.setPayMethod(order.getPayMethod());
        vo.setExpireTime(order.getExpireTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond());

        if (Constants.PayMethod.API.equals(order.getPayMethod())) {
            // API支付，返回二维码URL
            vo.setQrcodeUrl(qrcode.getQrcodeUrl());
        } else {
            // 页面跳转支付，跳转到商户前端支付页面
            vo.setPayPageUrl(pageDomain + "/pay/" + order.getOrderNo());
        }

        return vo;
    }

    @Override
    public PayOrder queryOrder(String orderNo) {
        return this.getOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getOrderNo, orderNo));
    }

    @Override
    public PayOrder queryOrderByOutTradeNo(String merchantNo, String outTradeNo) {
        Merchant merchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getMerchantNo, merchantNo));
        if (merchant == null) {
            return null;
        }

        return this.getOne(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getMerchantId, merchant.getId())
                .eq(PayOrder::getOutTradeNo, outTradeNo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmPay(String orderNo, Long merchantId) {
        PayOrder order = this.queryOrder(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 权限校验
        if (merchantId != null && !merchantId.equals(order.getMerchantId())) {
            throw new BusinessException("无权操作此订单");
        }

        // 状态校验
        if (!Constants.OrderStatus.UNPAID.equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        // 更新订单状态
        order.setStatus(Constants.OrderStatus.PAID);
        order.setPayAmount(order.getAmount());
        final LocalDateTime payTime = LocalDateTime.now();
        order.setPayTime(payTime);
        this.updateById(order);

        // 更新二维码统计
        PayQrcode qrcode = payQrcodeMapper.selectById(order.getQrcodeId());
        if (qrcode != null) {
            qrcode.setTotalAmount(qrcode.getTotalAmount().add(order.getPayAmount()));
            qrcode.setTotalCount(qrcode.getTotalCount() + 1);
            payQrcodeMapper.updateById(qrcode);
        }

        // 更新店铺统计
        Shop shop = shopMapper.selectById(order.getShopId());
        if (shop != null) {
            shop.setTotalAmount(shop.getTotalAmount().add(order.getPayAmount()));
            shop.setTotalCount(shop.getTotalCount() + 1);
            shopMapper.updateById(shop);
        }

        // 更新商户统计
        Merchant merchant = merchantMapper.selectById(order.getMerchantId());
        if (merchant != null) {
            merchant.setTotalAmount(merchant.getTotalAmount().add(order.getPayAmount()));
            merchant.setTotalCount(merchant.getTotalCount() + 1);
            merchantMapper.updateById(merchant);
        }

        log.info("订单支付确认成功: orderNo={}", orderNo);

        // 使用自定义线程池异步发送回调通知，提高响应速度
        final String finalOrderNo = orderNo;
        payNotifyExecutor.execute(() -> {
            try {
                doSendNotify(order);
            } catch (Exception e) {
                log.error("异步发送回调通知异常: orderNo={}", finalOrderNo, e);
            }
        });
    }

    @Override
    public void closeOrder(String orderNo, Long merchantId) {
        PayOrder order = this.queryOrder(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 权限校验
        if (merchantId != null && !merchantId.equals(order.getMerchantId())) {
            throw new BusinessException("无权操作此订单");
        }

        // 状态校验
        if (!Constants.OrderStatus.UNPAID.equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确");
        }

        order.setStatus(Constants.OrderStatus.CLOSED);
        this.updateById(order);
    }

    @Override
    public Page<PayOrder> pageOrders(Page<PayOrder> page, Long merchantId, Long shopId, String orderNo, Integer status) {
        LambdaQueryWrapper<PayOrder> wrapper = new LambdaQueryWrapper<>();
        if (merchantId != null) {
            wrapper.eq(PayOrder::getMerchantId, merchantId);
        }
        if (shopId != null) {
            wrapper.eq(PayOrder::getShopId, shopId);
        }
        if (StringUtils.hasText(orderNo)) {
            wrapper.and(w -> w.like(PayOrder::getOrderNo, orderNo)
                    .or().like(PayOrder::getOutTradeNo, orderNo));
        }
        if (status != null) {
            wrapper.eq(PayOrder::getStatus, status);
        }
        wrapper.orderByDesc(PayOrder::getCreateTime);

        Page<PayOrder> result = this.page(page, wrapper);

        // 填充商户和店铺名称
        result.getRecords().forEach(order -> {
            Merchant merchant = merchantMapper.selectById(order.getMerchantId());
            if (merchant != null) {
                order.setMerchantName(merchant.getMerchantName());
            }
            Shop shop = shopMapper.selectById(order.getShopId());
            if (shop != null) {
                order.setShopName(shop.getShopName());
            }
        });

        return result;
    }

    @Override
    public Page<PayOrder> pageMerchantOrders(Page<PayOrder> page, Long merchantId, Long shopId, Integer status) {
        return pageOrders(page, merchantId, shopId, null, status);
    }

    @Override
    public PayOrder getPayPageData(String orderNo) {
        PayOrder order = this.queryOrder(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单是否过期
        if (LocalDateTime.now().isAfter(order.getExpireTime())) {
            if (Constants.OrderStatus.UNPAID.equals(order.getStatus())) {
                order.setStatus(Constants.OrderStatus.EXPIRED);
                this.updateById(order);
            }
        }

        return order;
    }

    @Override
    public void processExpiredOrders() {
        // 查询过期的待支付订单
        List<PayOrder> expiredOrders = this.list(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getStatus, Constants.OrderStatus.UNPAID)
                .lt(PayOrder::getExpireTime, LocalDateTime.now()));

        for (PayOrder order : expiredOrders) {
            order.setStatus(Constants.OrderStatus.EXPIRED);
            this.updateById(order);
            log.info("订单已过期: orderNo={}", order.getOrderNo());
        }
    }

    @Override
    public void sendNotify(String orderNo) {
        PayOrder order = this.queryOrder(orderNo);
        // 使用自定义线程池异步发送
        payNotifyExecutor.execute(() -> {
            try {
                doSendNotify(order);
            } catch (Exception e) {
                log.error("异步发送回调通知异常: orderNo={}", order.getOrderNo(), e);
            }
        });
    }

    /**
     * 实际执行发送回调通知的方法
     */
    private void doSendNotify(PayOrder order) {
        long startTime = System.currentTimeMillis();
        if (order == null || !StringUtils.hasText(order.getNotifyUrl())) {
            return;
        }

        // 获取商户信息
        Merchant merchant = merchantMapper.selectById(order.getMerchantId());
        if (merchant == null) {
            return;
        }

        // 构建回调参数
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("merchantNo", merchant.getMerchantNo());
        params.put("orderNo", order.getOrderNo());
        params.put("outTradeNo", order.getOutTradeNo());
        params.put("amount", order.getAmount());
        params.put("payAmount", order.getPayAmount());
        params.put("payType", order.getPayType());
        params.put("status", String.valueOf(order.getStatus()));
        params.put("payTime", order.getPayTime()!=null?order.getPayTime().toString():"");
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        if (StringUtils.hasText(order.getExtParam())) {
            params.put("extParam", order.getExtParam());
        }

        // 生成签名
        String sign = SignUtil.generateSign(params, merchant.getApiSecret());
        params.put("sign", sign);

        try {
            // 发送回调，超时时间从10秒减少到5秒，提高响应速度
            String response = HttpUtil.post(order.getNotifyUrl(), params, 5000);
            log.info("发送回调通知成功: orderNo={}, response={}, 耗时{}ms",
                    order.getOrderNo(), response, System.currentTimeMillis() - startTime);

            // 更新通知状态
            order.setNotifyStatus("success".equalsIgnoreCase(response) ? 1 : 2);
            order.setNotifyCount(order.getNotifyCount() + 1);
            order.setLastNotifyTime(LocalDateTime.now());
            this.updateById(order);

        } catch (Exception e) {
            log.error("发送回调通知失败: orderNo={}, error={}, 耗时{}ms",
                    order.getOrderNo(), e.getMessage(), System.currentTimeMillis() - startTime);
            order.setNotifyStatus(2);
            order.setNotifyCount(order.getNotifyCount() + 1);
            order.setLastNotifyTime(LocalDateTime.now());
            this.updateById(order);
        }
    }

    @Override
    public void updateReturnUrl(String orderNo, String returnUrl) {
        PayOrder order = this.queryOrder(orderNo);
        if (order != null) {
            order.setReturnUrl(returnUrl);
            this.updateById(order);
            log.info("更新订单returnUrl: orderNo={}, returnUrl={}", orderNo, returnUrl);
        }
    }

    @Override
    public void resendNotify(String orderNo, Long merchantId) {
        PayOrder order = this.queryOrder(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 权限校验
        if (merchantId != null && !merchantId.equals(order.getMerchantId())) {
            throw new BusinessException("无权操作此订单");
        }

        // 状态校验：只有已支付的订单才能重发通知
        if (!Constants.OrderStatus.PAID.equals(order.getStatus())) {
            throw new BusinessException("订单状态不正确，只有已支付订单才能重发通知");
        }

        // 发送通知
        sendNotify(orderNo);
    }

    @Override
    public void processFailedNotify() {
        // 查询需要重发的订单：已支付、通知失败、通知次数小于5次
        List<PayOrder> failedOrders = this.list(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getStatus, Constants.OrderStatus.PAID)
                .eq(PayOrder::getNotifyStatus, 2)  // 通知失败
                .lt(PayOrder::getNotifyCount, 5)   // 通知次数小于5次
                .isNotNull(PayOrder::getNotifyUrl)
                .ne(PayOrder::getNotifyUrl, "")
                .isNotNull(PayOrder::getLastNotifyTime)
        );

        if (failedOrders.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        int retryCount = 0;
        
        for (PayOrder order : failedOrders) {
            // 根据通知次数计算重试间隔（递增策略：1分钟、2分钟、4分钟、8分钟、16分钟）
            int intervalMinutes = (int) Math.pow(2, order.getNotifyCount());
            LocalDateTime nextRetryTime = order.getLastNotifyTime().plusMinutes(intervalMinutes);
            
            // 如果还没到重试时间，跳过
            if (now.isBefore(nextRetryTime)) {
                continue;
            }
            
            try {
                doSendNotify(order);
                retryCount++;
                log.info("重发回调通知: orderNo={}, 第{}次重试", order.getOrderNo(), order.getNotifyCount());
            } catch (Exception e) {
                log.error("重发回调通知失败: orderNo={}, error={}", order.getOrderNo(), e.getMessage());
            }
        }
        
        if (retryCount > 0) {
            log.info("本次处理失败回调通知完成，共重发{}条", retryCount);
        }
    }
}
