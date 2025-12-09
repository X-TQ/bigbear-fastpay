package com.fastpay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fastpay.entity.PayOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 支付订单 Mapper 接口
 *
 * @author FastPay
 */
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder> {

    /**
     * 统计今日订单数量
     */
    @Select("SELECT COUNT(*) FROM fp_pay_order WHERE DATE(create_time) = CURDATE()")
    Integer countTodayOrders();

    /**
     * 统计今日成功订单金额
     */
    @Select("SELECT IFNULL(SUM(pay_amount), 0) FROM fp_pay_order WHERE DATE(create_time) = CURDATE() AND status = 1")
    BigDecimal sumTodayAmount();

    /**
     * 统计今日成功订单数量
     */
    @Select("SELECT COUNT(*) FROM fp_pay_order WHERE DATE(create_time) = CURDATE() AND status = 1")
    Integer countTodaySuccessOrders();

    /**
     * 按商户统计订单
     */
    @Select("SELECT merchant_id, COUNT(*) as order_count, IFNULL(SUM(pay_amount), 0) as total_amount " +
            "FROM fp_pay_order WHERE status = 1 GROUP BY merchant_id")
    List<Map<String, Object>> statsByMerchant();

    /**
     * 按日期统计最近N天的订单
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as order_count, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as success_count, " +
            "IFNULL(SUM(CASE WHEN status = 1 THEN pay_amount ELSE 0 END), 0) as total_amount " +
            "FROM fp_pay_order WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(create_time) ORDER BY date DESC")
    List<Map<String, Object>> statsByDate(@Param("days") Integer days);
}
