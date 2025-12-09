package com.fastpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fastpay.common.BusinessException;
import com.fastpay.common.Constants;
import com.fastpay.dto.ShopDTO;
import com.fastpay.entity.Merchant;
import com.fastpay.entity.PayQrcode;
import com.fastpay.entity.Shop;
import com.fastpay.mapper.MerchantMapper;
import com.fastpay.mapper.PayQrcodeMapper;
import com.fastpay.mapper.ShopMapper;
import com.fastpay.service.ShopService;
import com.fastpay.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 店铺服务实现类
 *
 * @author FastPay
 */
@Slf4j
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {

    private final MerchantMapper merchantMapper;
    private final PayQrcodeMapper payQrcodeMapper;

    public ShopServiceImpl(MerchantMapper merchantMapper, PayQrcodeMapper payQrcodeMapper) {
        this.merchantMapper = merchantMapper;
        this.payQrcodeMapper = payQrcodeMapper;
    }

    @Override
    public Shop createShop(ShopDTO dto, Long merchantId) {
        // 使用传入的merchantId或dto中的merchantId
        Long actualMerchantId = merchantId != null ? merchantId : dto.getMerchantId();
        if (actualMerchantId == null) {
            throw new BusinessException("商户ID不能为空");
        }

        // 验证商户是否存在
        Merchant merchant = merchantMapper.selectById(actualMerchantId);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }

        // 创建店铺
        Shop shop = new Shop();
        shop.setMerchantId(actualMerchantId);
        shop.setShopNo(SignUtil.generateShopNo());
        shop.setShopName(dto.getShopName());
        shop.setDescription(dto.getDescription());
        shop.setContactName(dto.getContactName());
        shop.setContactPhone(dto.getContactPhone());
        shop.setTotalAmount(BigDecimal.ZERO);
        shop.setTotalCount(0);
        shop.setStatus(Constants.Status.ENABLED);

        this.save(shop);
        log.info("创建店铺成功: {}", shop.getShopNo());

        return shop;
    }

    @Override
    public void updateShop(ShopDTO dto, Long merchantId) {
        Shop shop = this.getById(dto.getId());
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }

        // 权限校验
        if (merchantId != null && !merchantId.equals(shop.getMerchantId())) {
            throw new BusinessException("无权操作此店铺");
        }

        // 更新信息
        if (StringUtils.hasText(dto.getShopName())) {
            shop.setShopName(dto.getShopName());
        }
        shop.setDescription(dto.getDescription());
        shop.setContactName(dto.getContactName());
        shop.setContactPhone(dto.getContactPhone());

        this.updateById(shop);
    }

    @Override
    public void updateStatus(Long id, Integer status, Long merchantId) {
        Shop shop = this.getById(id);
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }

        // 权限校验
        if (merchantId != null && !merchantId.equals(shop.getMerchantId())) {
            throw new BusinessException("无权操作此店铺");
        }

        shop.setStatus(status);
        this.updateById(shop);
    }

    @Override
    public void deleteShop(Long id, Long merchantId) {
        Shop shop = this.getById(id);
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }

        // 权限校验
        if (merchantId != null && !merchantId.equals(shop.getMerchantId())) {
            throw new BusinessException("无权操作此店铺");
        }

        this.removeById(id);
    }

    @Override
    public Page<Shop> pageShops(Page<Shop> page, Long merchantId, String shopName) {
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        if (merchantId != null) {
            wrapper.eq(Shop::getMerchantId, merchantId);
        }
        if (StringUtils.hasText(shopName)) {
            wrapper.like(Shop::getShopName, shopName);
        }
        // 按累计交易金额降序，再按创建时间降序
        wrapper.orderByDesc(Shop::getTotalAmount)
               .orderByDesc(Shop::getCreateTime);

        Page<Shop> result = this.page(page, wrapper);

        // 填充商户名称和二维码数量
        result.getRecords().forEach(shop -> {
            Merchant merchant = merchantMapper.selectById(shop.getMerchantId());
            if (merchant != null) {
                shop.setMerchantName(merchant.getMerchantName());
            }
            // 填充二维码数量
            Long count = payQrcodeMapper.selectCount(
                new LambdaQueryWrapper<PayQrcode>()
                    .eq(PayQrcode::getShopId, shop.getId())
            );
            shop.setQrcodeCount(count.intValue());
        });

        return result;
    }

    @Override
    public List<Shop> listByMerchant(Long merchantId) {
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<Shop>()
                // 按累计交易金额降序，再按创建时间降序
                .orderByDesc(Shop::getTotalAmount)
                .orderByDesc(Shop::getCreateTime);
        if (merchantId != null) {
            wrapper.eq(Shop::getMerchantId, merchantId);
        }
        List<Shop> shops = this.list(wrapper);
        
        // 填充每个店铺的二维码数量
        shops.forEach(shop -> {
            Long count = payQrcodeMapper.selectCount(
                new LambdaQueryWrapper<PayQrcode>()
                    .eq(PayQrcode::getShopId, shop.getId())
            );
            shop.setQrcodeCount(count.intValue());
        });
        
        return shops;
    }

    @Override
    public Shop getShopDetail(Long id, Long merchantId) {
        Shop shop = this.getById(id);
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }

        // 权限校验
        if (merchantId != null && !merchantId.equals(shop.getMerchantId())) {
            throw new BusinessException("无权查看此店铺");
        }

        // 填充商户名称
        Merchant merchant = merchantMapper.selectById(shop.getMerchantId());
        if (merchant != null) {
            shop.setMerchantName(merchant.getMerchantName());
        }

        return shop;
    }
}
