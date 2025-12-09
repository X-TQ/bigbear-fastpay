package com.fastpay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fastpay.entity.MerchantChannel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户通道 Mapper 接口
 *
 * @author FastPay
 */
@Mapper
public interface MerchantChannelMapper extends BaseMapper<MerchantChannel> {
}
