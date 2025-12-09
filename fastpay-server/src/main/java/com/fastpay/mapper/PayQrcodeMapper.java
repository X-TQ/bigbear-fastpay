package com.fastpay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fastpay.entity.PayQrcode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收款二维码 Mapper 接口
 *
 * @author FastPay
 */
@Mapper
public interface PayQrcodeMapper extends BaseMapper<PayQrcode> {
}
