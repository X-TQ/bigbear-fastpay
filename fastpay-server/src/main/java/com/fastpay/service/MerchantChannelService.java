package com.fastpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fastpay.dto.MerchantChannelDTO;
import com.fastpay.entity.MerchantChannel;

import java.util.List;

/**
 * 商户通道服务接口
 *
 * @author FastPay
 */
public interface MerchantChannelService extends IService<MerchantChannel> {

    /**
     * 获取商户的通道列表
     *
     * @param merchantId 商户ID
     * @return 通道列表
     */
    List<MerchantChannel> listByMerchant(Long merchantId);

    /**
     * 获取商户指定支付类型的启用通道列表
     *
     * @param merchantId 商户ID
     * @param payType    支付类型
     * @return 通道列表
     */
    List<MerchantChannel> listByMerchantAndPayType(Long merchantId, String payType);

    /**
     * 添加通道
     *
     * @param dto        通道信息
     * @param merchantId 商户ID
     * @return 通道
     */
    MerchantChannel addChannel(MerchantChannelDTO dto, Long merchantId);

    /**
     * 更新通道
     *
     * @param dto        通道信息
     * @param merchantId 商户ID
     */
    void updateChannel(MerchantChannelDTO dto, Long merchantId);

    /**
     * 删除通道
     *
     * @param id         通道ID
     * @param merchantId 商户ID
     */
    void deleteChannel(Long id, Long merchantId);

    /**
     * 更新通道状态
     *
     * @param id         通道ID
     * @param status     状态
     * @param merchantId 商户ID
     */
    void updateStatus(Long id, Integer status, Long merchantId);

    /**
     * 获取通道详情（验证商户归属）
     *
     * @param id         通道ID
     * @param merchantId 商户ID
     * @return 通道
     */
    MerchantChannel getByIdAndMerchant(Long id, Long merchantId);

    /**
     * 生成默认模版
     *
     * @param channelId 通道ID
     * @return 模版内容
     */
    String generateDefaultTemplate(Long channelId);
}
