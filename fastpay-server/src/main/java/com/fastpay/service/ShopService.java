package com.fastpay.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fastpay.dto.ShopDTO;
import com.fastpay.entity.Shop;

import java.util.List;

/**
 * 店铺服务接口
 *
 * @author FastPay
 */
public interface ShopService extends IService<Shop> {

    /**
     * 创建店铺
     *
     * @param dto        店铺信息
     * @param merchantId 商户ID
     * @return 创建的店铺
     */
    Shop createShop(ShopDTO dto, Long merchantId);

    /**
     * 更新店铺信息
     *
     * @param dto        店铺信息
     * @param merchantId 商户ID（用于权限校验）
     */
    void updateShop(ShopDTO dto, Long merchantId);

    /**
     * 更新店铺状态
     *
     * @param id         店铺ID
     * @param status     状态
     * @param merchantId 商户ID（用于权限校验，管理员传null）
     */
    void updateStatus(Long id, Integer status, Long merchantId);

    /**
     * 删除店铺
     *
     * @param id         店铺ID
     * @param merchantId 商户ID（用于权限校验，管理员传null）
     */
    void deleteShop(Long id, Long merchantId);

    /**
     * 分页查询店铺列表（管理员）
     *
     * @param page       分页参数
     * @param merchantId 商户ID
     * @param shopName   店铺名称
     * @return 分页结果
     */
    Page<Shop> pageShops(Page<Shop> page, Long merchantId, String shopName);

    /**
     * 获取商户的店铺列表
     *
     * @param merchantId 商户ID
     * @return 店铺列表
     */
    List<Shop> listByMerchant(Long merchantId);

    /**
     * 获取店铺详情
     *
     * @param id         店铺ID
     * @param merchantId 商户ID（用于权限校验，管理员传null）
     * @return 店铺信息
     */
    Shop getShopDetail(Long id, Long merchantId);
}
