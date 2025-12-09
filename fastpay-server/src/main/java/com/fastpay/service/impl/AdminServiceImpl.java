package com.fastpay.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fastpay.common.BusinessException;
import com.fastpay.common.Constants;
import com.fastpay.dto.LoginDTO;
import com.fastpay.entity.Admin;
import com.fastpay.entity.PayOrder;
import com.fastpay.mapper.*;
import com.fastpay.service.AdminService;
import com.fastpay.util.JwtUtil;
import com.fastpay.vo.DashboardVO;
import com.fastpay.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员服务实现类
 *
 * @author FastPay
 */
@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final JwtUtil jwtUtil;
    private final MerchantMapper merchantMapper;
    private final ShopMapper shopMapper;
    private final PayQrcodeMapper payQrcodeMapper;
    private final PayOrderMapper payOrderMapper;

    @Value("${fastpay.admin.username}")
    private String defaultUsername;

    @Value("${fastpay.admin.password}")
    private String defaultPassword;

    public AdminServiceImpl(JwtUtil jwtUtil, MerchantMapper merchantMapper, 
                           ShopMapper shopMapper, PayQrcodeMapper payQrcodeMapper,
                           PayOrderMapper payOrderMapper) {
        this.jwtUtil = jwtUtil;
        this.merchantMapper = merchantMapper;
        this.shopMapper = shopMapper;
        this.payQrcodeMapper = payQrcodeMapper;
        this.payOrderMapper = payOrderMapper;
    }

    @Override
    public LoginVO login(LoginDTO dto, String ip) {
        // 查询管理员
        Admin admin = this.getOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, dto.getUsername()));

        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        String encryptedPassword = SecureUtil.md5(dto.getPassword());
        if (!encryptedPassword.equals(admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查状态
        if (!Constants.Status.ENABLED.equals(admin.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }

        // 更新登录信息
        admin.setLastLoginTime(LocalDateTime.now());
        admin.setLastLoginIp(ip);
        this.updateById(admin);

        // 生成Token
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), "admin");

        // 构建返回结果
        LoginVO vo = new LoginVO();
        vo.setUserId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setNickname(admin.getNickname());
        vo.setAvatar(admin.getAvatar());
        vo.setUserType("admin");
        vo.setToken(token);

        return vo;
    }

    @Override
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();

        // 今日统计
        vo.setTodayOrderCount(payOrderMapper.countTodayOrders());
        vo.setTodaySuccessCount(payOrderMapper.countTodaySuccessOrders());
        vo.setTodayAmount(payOrderMapper.sumTodayAmount());

        // 总计统计
        vo.setTotalOrderCount(payOrderMapper.selectCount(null));
        vo.setTotalSuccessCount(payOrderMapper.selectCount(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getStatus, Constants.OrderStatus.PAID)));
        
        // 计算总交易金额
        LambdaQueryWrapper<PayOrder> amountQuery = new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getStatus, Constants.OrderStatus.PAID);
        BigDecimal totalAmount = payOrderMapper.selectList(amountQuery).stream()
                .map(PayOrder::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalAmount(totalAmount);

        // 商户、店铺、二维码数量
        vo.setMerchantCount(merchantMapper.selectCount(null));
        vo.setShopCount(shopMapper.selectCount(null));
        vo.setQrcodeCount(payQrcodeMapper.selectCount(null));

        // 支付方式统计（成功订单）
        LambdaQueryWrapper<PayOrder> wxpayQuery = new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getStatus, Constants.OrderStatus.PAID)
                .eq(PayOrder::getPayType, "wxpay");
        vo.setWxpayCount(payOrderMapper.selectCount(wxpayQuery));
        BigDecimal wxpayAmount = payOrderMapper.selectList(wxpayQuery).stream()
                .map(PayOrder::getPayAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setWxpayAmount(wxpayAmount);

        LambdaQueryWrapper<PayOrder> alipayQuery = new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getStatus, Constants.OrderStatus.PAID)
                .eq(PayOrder::getPayType, "alipay");
        vo.setAlipayCount(payOrderMapper.selectCount(alipayQuery));
        BigDecimal alipayAmount = payOrderMapper.selectList(alipayQuery).stream()
                .map(PayOrder::getPayAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setAlipayAmount(alipayAmount);

        // 最近7天统计
        vo.setRecentStats(payOrderMapper.statsByDate(7));

        return vo;
    }

    @Override
    public void initDefaultAdmin() {
        // 检查是否已存在管理员
        Long count = this.count();
        if (count > 0) {
            return;
        }

        // 创建默认管理员
        Admin admin = new Admin();
        admin.setUsername(defaultUsername);
        admin.setPassword(SecureUtil.md5(defaultPassword));
        admin.setNickname("超级管理员");
        admin.setAvatar("/static/avatar/admin.png");
        admin.setStatus(Constants.Status.ENABLED);
        this.save(admin);

        log.info("初始化默认管理员账号: {}", defaultUsername);
    }
}
