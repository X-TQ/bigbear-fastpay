package com.fastpay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fastpay.entity.Shop;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店铺 Mapper 接口
 *
 * @author FastPay
 */
@Mapper
public interface ShopMapper extends BaseMapper<Shop> {
}
