package com.fastpay.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fastpay.dto.PayQrcodeDTO;
import com.fastpay.entity.PayQrcode;

import java.util.List;

/**
 * 收款二维码服务接口
 *
 * @author FastPay
 */
public interface PayQrcodeService extends IService<PayQrcode> {

    /**
     * 添加收款二维码
     *
     * @param dto        二维码信息
     * @param merchantId 商户ID
     * @return 创建的二维码
     */
    PayQrcode addQrcode(PayQrcodeDTO dto, Long merchantId);

    /**
     * 更新收款二维码
     *
     * @param dto        二维码信息
     * @param merchantId 商户ID
     */
    void updateQrcode(PayQrcodeDTO dto, Long merchantId);

    /**
     * 更新二维码状态
     *
     * @param id         二维码ID
     * @param status     状态
     * @param merchantId 商户ID
     */
    void updateStatus(Long id, Integer status, Long merchantId);

    /**
     * 删除二维码
     *
     * @param id         二维码ID
     * @param merchantId 商户ID
     */
    void deleteQrcode(Long id, Long merchantId);

    /**
     * 分页查询二维码列表
     *
     * @param page       分页参数
     * @param merchantId 商户ID
     * @param shopId     店铺ID
     * @param payType    支付类型
     * @return 分页结果
     */
    Page<PayQrcode> pageQrcodes(Page<PayQrcode> page, Long merchantId, Long shopId, String payType);

    /**
     * 获取店铺的二维码列表
     *
     * @param shopId     店铺ID
     * @param merchantId 商户ID
     * @return 二维码列表
     */
    List<PayQrcode> listByShop(Long shopId, Long merchantId);

    /**
     * 获取可用的收款二维码（用于支付）
     *
     * @param merchantId 商户ID
     * @param shopNo 店铺编码
     * @param payType    支付类型
     * @return 可用的二维码
     */
    PayQrcode getAvailableQrcode(Long merchantId, String shopNo, String payType);
}
