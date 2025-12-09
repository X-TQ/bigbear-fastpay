package com.fastpay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fastpay.common.BusinessException;
import com.fastpay.common.Constants;
import com.fastpay.dto.MerchantChannelDTO;
import com.fastpay.entity.MerchantChannel;
import com.fastpay.mapper.MerchantChannelMapper;
import com.fastpay.service.MerchantChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商户通道服务实现类
 *
 * @author FastPay
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantChannelServiceImpl extends ServiceImpl<MerchantChannelMapper, MerchantChannel>
        implements MerchantChannelService {

    @Override
    public List<MerchantChannel> listByMerchant(Long merchantId) {
        return list(new LambdaQueryWrapper<MerchantChannel>()
                .eq(MerchantChannel::getMerchantId, merchantId)
                .orderByDesc(MerchantChannel::getCreateTime));
    }

    @Override
    public List<MerchantChannel> listByMerchantAndPayType(Long merchantId, String payType) {
        return list(new LambdaQueryWrapper<MerchantChannel>()
                .eq(MerchantChannel::getMerchantId, merchantId)
                .eq(MerchantChannel::getPayType, payType)
                .eq(MerchantChannel::getStatus, Constants.Status.ENABLED)
                .orderByDesc(MerchantChannel::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantChannel addChannel(MerchantChannelDTO dto, Long merchantId) {
        // 验证支付类型
        if (!Constants.PayType.WXPAY.equals(dto.getPayType()) && !Constants.PayType.ALIPAY.equals(dto.getPayType())) {
            throw new BusinessException("不支持的支付类型");
        }

        MerchantChannel channel = new MerchantChannel();
        channel.setMerchantId(merchantId);
        channel.setChannelName(dto.getChannelName());
        channel.setPayType(dto.getPayType());
        channel.setRemark(dto.getRemark());
        channel.setStatus(Constants.Status.ENABLED);

        // 保存后生成默认模版
        save(channel);

        // 设置默认模版
        if (dto.getTemplate() == null || dto.getTemplate().isBlank()) {
            channel.setTemplate(generateDefaultTemplate(channel.getId()));
            updateById(channel);
        } else {
            channel.setTemplate(dto.getTemplate());
            updateById(channel);
        }

        log.info("添加商户通道成功，merchantId={}, channelId={}, payType={}", merchantId, channel.getId(), dto.getPayType());
        return channel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChannel(MerchantChannelDTO dto, Long merchantId) {
        MerchantChannel channel = getByIdAndMerchant(dto.getId(), merchantId);
        if (channel == null) {
            throw new BusinessException("通道不存在");
        }

        // 验证支付类型
        if (!Constants.PayType.WXPAY.equals(dto.getPayType()) && !Constants.PayType.ALIPAY.equals(dto.getPayType())) {
            throw new BusinessException("不支持的支付类型");
        }

        channel.setChannelName(dto.getChannelName());
        channel.setPayType(dto.getPayType());
        channel.setTemplate(dto.getTemplate());
        channel.setRemark(dto.getRemark());
        updateById(channel);

        log.info("更新商户通道成功，merchantId={}, channelId={}", merchantId, dto.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChannel(Long id, Long merchantId) {
        MerchantChannel channel = getByIdAndMerchant(id, merchantId);
        if (channel == null) {
            throw new BusinessException("通道不存在");
        }

        // TODO: 检查是否有二维码使用此通道

        removeById(id);
        log.info("删除商户通道成功，merchantId={}, channelId={}", merchantId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status, Long merchantId) {
        MerchantChannel channel = getByIdAndMerchant(id, merchantId);
        if (channel == null) {
            throw new BusinessException("通道不存在");
        }

        channel.setStatus(status);
        updateById(channel);
        log.info("更新商户通道状态成功，merchantId={}, channelId={}, status={}", merchantId, id, status);
    }

    @Override
    public MerchantChannel getByIdAndMerchant(Long id, Long merchantId) {
        return getOne(new LambdaQueryWrapper<MerchantChannel>()
                .eq(MerchantChannel::getId, id)
                .eq(MerchantChannel::getMerchantId, merchantId));
    }

    @Override
    public String generateDefaultTemplate(Long channelId) {
        return String.format(
                "{\"channelId\":%d,\"title\":\"[title]\",\"msg\":\"[msg]\",\"receiveTime\":\"[receive_time]\",\"timestamp\":\"[timestamp]\",\"sign\":\"[sign]\"}",
                channelId
        );
    }
}
