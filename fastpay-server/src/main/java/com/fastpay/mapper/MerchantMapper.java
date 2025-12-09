package com.fastpay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fastpay.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户 Mapper 接口
 *
 * @author FastPay
 */
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
}
