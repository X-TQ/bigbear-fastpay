package com.fastpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fastpay.common.BusinessException;
import com.fastpay.common.Constants;
import com.fastpay.dto.PayQrcodeDTO;
import com.fastpay.entity.MerchantChannel;
import com.fastpay.entity.PayQrcode;
import com.fastpay.entity.Shop;
import com.fastpay.mapper.MerchantChannelMapper;
import com.fastpay.mapper.PayQrcodeMapper;
import com.fastpay.mapper.ShopMapper;
import com.fastpay.service.PayQrcodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收款二维码服务实现类
 *
 * @author FastPay
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayQrcodeServiceImpl extends ServiceImpl<PayQrcodeMapper, PayQrcode> implements PayQrcodeService {

    private final ShopMapper shopMapper;
    private final MerchantChannelMapper merchantChannelMapper;

    @Override
    public PayQrcode addQrcode(PayQrcodeDTO dto, Long merchantId) {
        // 验证店铺
        Shop shop = shopMapper.selectById(dto.getShopId());
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }

        // 权限校验
        if (!shop.getMerchantId().equals(merchantId)) {
            throw new BusinessException("无权操作此店铺");
        }

        // 验证通道
        MerchantChannel channel = merchantChannelMapper.selectById(dto.getChannelId());
        if (channel == null) {
            throw new BusinessException("通道不存在");
        }
        if (!channel.getMerchantId().equals(merchantId)) {
            throw new BusinessException("无权使用此通道");
        }
        if (!Constants.Status.ENABLED.equals(channel.getStatus())) {
            throw new BusinessException("通道未启用");
        }

        // 如果前端传了 payType，校验是否与通道匹配
        if (StringUtils.hasText(dto.getPayType()) && !dto.getPayType().equals(channel.getPayType())) {
            throw new BusinessException("支付类型与通道不匹配");
        }

        // 创建二维码记录
        PayQrcode qrcode = new PayQrcode();
        qrcode.setMerchantId(merchantId);
        qrcode.setShopId(dto.getShopId());
        qrcode.setChannelId(String.valueOf(dto.getChannelId()));
        qrcode.setPayType(channel.getPayType());  // 使用通道的支付类型
        qrcode.setQrcodeName(dto.getQrcodeName());
        qrcode.setQrcodeUrl(dto.getQrcodeUrl());
        qrcode.setTotalAmount(BigDecimal.ZERO);
        qrcode.setTotalCount(0);
        qrcode.setStatus(Constants.Status.ENABLED);
        qrcode.setSortWeight(dto.getSortWeight() != null ? dto.getSortWeight() : 0);

        this.save(qrcode);
        log.info("添加收款二维码成功: shopId={}, channelId={}, payType={}", dto.getShopId(), dto.getChannelId(), channel.getPayType());

        return qrcode;
    }

    @Override
    public void updateQrcode(PayQrcodeDTO dto, Long merchantId) {
        PayQrcode qrcode = this.getById(dto.getId());
        if (qrcode == null) {
            throw new BusinessException("二维码不存在");
        }

        // 权限校验
        if (!qrcode.getMerchantId().equals(merchantId)) {
            throw new BusinessException("无权操作此二维码");
        }

        // 如果更新了图片
        if (StringUtils.hasText(dto.getQrcodeUrl()) && !dto.getQrcodeUrl().equals(qrcode.getQrcodeUrl())) {
            qrcode.setQrcodeUrl(dto.getQrcodeUrl());
        }

        // 更新其他信息
        if (StringUtils.hasText(dto.getQrcodeName())) {
            qrcode.setQrcodeName(dto.getQrcodeName());
        }
        if (dto.getSortWeight() != null) {
            qrcode.setSortWeight(dto.getSortWeight());
        }

        this.updateById(qrcode);
    }

    @Override
    public void updateStatus(Long id, Integer status, Long merchantId) {
        PayQrcode qrcode = this.getById(id);
        if (qrcode == null) {
            throw new BusinessException("二维码不存在");
        }

        // 权限校验
        if (!qrcode.getMerchantId().equals(merchantId)) {
            throw new BusinessException("无权操作此二维码");
        }

        qrcode.setStatus(status);
        this.updateById(qrcode);
    }

    @Override
    public void deleteQrcode(Long id, Long merchantId) {
        PayQrcode qrcode = this.getById(id);
        if (qrcode == null) {
            throw new BusinessException("二维码不存在");
        }

        // 权限校验
        if (!qrcode.getMerchantId().equals(merchantId)) {
            throw new BusinessException("无权操作此二维码");
        }

        this.removeById(id);
    }

    @Override
    public Page<PayQrcode> pageQrcodes(Page<PayQrcode> page, Long merchantId, Long shopId, String payType) {
        LambdaQueryWrapper<PayQrcode> wrapper = new LambdaQueryWrapper<>();
        if (merchantId != null) {
            wrapper.eq(PayQrcode::getMerchantId, merchantId);
        }
        if (shopId != null) {
            wrapper.eq(PayQrcode::getShopId, shopId);
        }
        if (StringUtils.hasText(payType)) {
            wrapper.eq(PayQrcode::getPayType, payType);
        }
        wrapper.orderByDesc(PayQrcode::getSortWeight)
               .orderByDesc(PayQrcode::getCreateTime);

        Page<PayQrcode> result = this.page(page, wrapper);

        // 填充店铺名称
        result.getRecords().forEach(qrcode -> {
            Shop shop = shopMapper.selectById(qrcode.getShopId());
            if (shop != null) {
                qrcode.setShopName(shop.getShopName());
            }
        });

        return result;
    }

    @Override
    public List<PayQrcode> listByShop(Long shopId, Long merchantId) {
        LambdaQueryWrapper<PayQrcode> wrapper = new LambdaQueryWrapper<PayQrcode>()
                .eq(PayQrcode::getShopId, shopId)
                .eq(PayQrcode::getStatus, Constants.Status.ENABLED)
                .orderByDesc(PayQrcode::getSortWeight)
                .orderByDesc(PayQrcode::getCreateTime);

        if (merchantId != null) {
            wrapper.eq(PayQrcode::getMerchantId, merchantId);
        }

        return this.list(wrapper);
    }

    @Override
    public PayQrcode getAvailableQrcode(Long merchantId, String payType) {
        LambdaQueryWrapper<PayQrcode> wrapper = new LambdaQueryWrapper<PayQrcode>()
                .eq(PayQrcode::getMerchantId, merchantId)
                .eq(PayQrcode::getStatus, Constants.Status.ENABLED)
                .orderByDesc(PayQrcode::getSortWeight)
                .orderByAsc(PayQrcode::getTotalCount)  // 优先使用次数少的
                .last("LIMIT 1");

        if (StringUtils.hasText(payType)) {
            wrapper.eq(PayQrcode::getPayType, payType);
        }

        return this.getOne(wrapper);
    }

}
