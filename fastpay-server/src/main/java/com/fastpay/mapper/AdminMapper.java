package com.fastpay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fastpay.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员 Mapper 接口
 *
 * @author FastPay
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
