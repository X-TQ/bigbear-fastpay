package com.fastpay.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fastpay.common.BusinessException;
import com.fastpay.common.Constants;
import com.fastpay.dto.LoginDTO;
import com.fastpay.dto.MerchantDTO;
import com.fastpay.entity.Merchant;
import com.fastpay.entity.PayOrder;
import com.fastpay.entity.PayQrcode;
import com.fastpay.entity.Shop;
import com.fastpay.mapper.MerchantMapper;
import com.fastpay.mapper.PayOrderMapper;
import com.fastpay.mapper.PayQrcodeMapper;
import com.fastpay.mapper.ShopMapper;
import com.fastpay.service.MerchantService;
import com.fastpay.util.JwtUtil;
import com.fastpay.util.SignUtil;
import com.fastpay.vo.DashboardVO;
import com.fastpay.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商户服务实现类
 *
 * @author FastPay
 */
@Slf4j
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    private final JwtUtil jwtUtil;
    private final ShopMapper shopMapper;
    private final PayQrcodeMapper payQrcodeMapper;
    private final PayOrderMapper payOrderMapper;

    public MerchantServiceImpl(JwtUtil jwtUtil, ShopMapper shopMapper,
                               PayQrcodeMapper payQrcodeMapper, PayOrderMapper payOrderMapper) {
        this.jwtUtil = jwtUtil;
        this.shopMapper = shopMapper;
        this.payQrcodeMapper = payQrcodeMapper;
        this.payOrderMapper = payOrderMapper;
    }

    @Override
    public LoginVO login(LoginDTO dto, String ip) {
        // 查询商户
        Merchant merchant = this.getOne(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getUsername, dto.getUsername()));

        if (merchant == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        String encryptedPassword = SecureUtil.md5(dto.getPassword());
        if (!encryptedPassword.equals(merchant.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查状态
        if (Constants.Status.DISABLED.equals(merchant.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }
        if (Constants.Status.PENDING.equals(merchant.getStatus())) {
            throw new BusinessException("账号待审核，请联系管理员");
        }

        // 更新登录信息
        merchant.setLastLoginTime(LocalDateTime.now());
        merchant.setLastLoginIp(ip);
        this.updateById(merchant);

        // 生成Token
        String token = jwtUtil.generateToken(merchant.getId(), merchant.getUsername(), "merchant");

        // 构建返回结果
        LoginVO vo = new LoginVO();
        vo.setUserId(merchant.getId());
        vo.setUsername(merchant.getUsername());
        vo.setNickname(merchant.getMerchantName());
        vo.setAvatar("/static/avatar/merchant.png");
        vo.setUserType("merchant");
        vo.setToken(token);
        vo.setMerchantNo(merchant.getMerchantNo());
        vo.setApiKey(merchant.getApiKey());

        return vo;
    }

    @Override
    public Merchant createMerchant(MerchantDTO dto) {
        // 检查用户名是否已存在
        Long count = this.count(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 创建商户
        Merchant merchant = new Merchant();
        merchant.setMerchantNo(SignUtil.generateMerchantNo());
        merchant.setMerchantName(dto.getMerchantName());
        merchant.setUsername(dto.getUsername());
        merchant.setPassword(SecureUtil.md5(dto.getPassword()));
        merchant.setContactName(dto.getContactName());
        merchant.setContactPhone(dto.getContactPhone());
        merchant.setContactEmail(dto.getContactEmail());
        merchant.setApiKey(SignUtil.generateApiKey());
        merchant.setApiSecret(SignUtil.generateApiSecret());
        merchant.setNotifyUrl(dto.getNotifyUrl());
        merchant.setReturnUrl(dto.getReturnUrl());
        merchant.setTotalAmount(BigDecimal.ZERO);
        merchant.setTotalCount(0);
        merchant.setStatus(Constants.Status.ENABLED);
        merchant.setRemark(dto.getRemark());

        this.save(merchant);
        log.info("创建商户成功: {}", merchant.getMerchantNo());

        return merchant;
    }

    @Override
    public void updateMerchant(MerchantDTO dto) {
        Merchant merchant = this.getById(dto.getId());
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }

        // 检查用户名是否被其他商户使用
        if (StringUtils.hasText(dto.getUsername()) && !dto.getUsername().equals(merchant.getUsername())) {
            Long count = this.count(new LambdaQueryWrapper<Merchant>()
                    .eq(Merchant::getUsername, dto.getUsername())
                    .ne(Merchant::getId, dto.getId()));
            if (count > 0) {
                throw new BusinessException("用户名已被使用");
            }
            merchant.setUsername(dto.getUsername());
        }

        // 更新信息
        if (StringUtils.hasText(dto.getMerchantName())) {
            merchant.setMerchantName(dto.getMerchantName());
        }
        if (StringUtils.hasText(dto.getPassword())) {
            merchant.setPassword(SecureUtil.md5(dto.getPassword()));
        }
        if (StringUtils.hasText(dto.getContactName())) {
            merchant.setContactName(dto.getContactName());
        }
        if (StringUtils.hasText(dto.getContactPhone())) {
            merchant.setContactPhone(dto.getContactPhone());
        }
        merchant.setContactEmail(dto.getContactEmail());
        merchant.setNotifyUrl(dto.getNotifyUrl());
        merchant.setReturnUrl(dto.getReturnUrl());
        merchant.setRemark(dto.getRemark());

        this.updateById(merchant);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Merchant merchant = this.getById(id);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }

        merchant.setStatus(status);
        this.updateById(merchant);
    }

    @Override
    public Page<Merchant> pageMerchants(Page<Merchant> page, String merchantName, Integer status) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(merchantName)) {
            wrapper.like(Merchant::getMerchantName, merchantName);
        }
        if (status != null) {
            wrapper.eq(Merchant::getStatus, status);
        }
        wrapper.orderByDesc(Merchant::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<Merchant> listAllMerchants() {
        return this.list(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getStatus, Constants.Status.ENABLED)
                .orderByDesc(Merchant::getCreateTime));
    }

    @Override
    public Merchant resetApiKey(Long id) {
        Merchant merchant = this.getById(id);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }

        merchant.setApiKey(SignUtil.generateApiKey());
        merchant.setApiSecret(SignUtil.generateApiSecret());
        this.updateById(merchant);

        return merchant;
    }

    @Override
    public DashboardVO getMerchantDashboard(Long merchantId) {
        DashboardVO vo = new DashboardVO();

        // 查询条件
        LambdaQueryWrapper<PayOrder> baseQuery = new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getMerchantId, merchantId);

        // 今日统计
        LambdaQueryWrapper<PayOrder> todayQuery = new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getMerchantId, merchantId)
                .apply("DATE(create_time) = CURDATE()");
        vo.setTodayOrderCount(Math.toIntExact(payOrderMapper.selectCount(todayQuery)));

        LambdaQueryWrapper<PayOrder> todaySuccessQuery = new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getMerchantId, merchantId)
                .eq(PayOrder::getStatus, Constants.OrderStatus.PAID)
                .apply("DATE(create_time) = CURDATE()");
        vo.setTodaySuccessCount(Math.toIntExact(payOrderMapper.selectCount(todaySuccessQuery)));

        // 今日金额
        List<PayOrder> todayOrders = payOrderMapper.selectList(todaySuccessQuery);
        BigDecimal todayAmount = todayOrders.stream()
                .map(PayOrder::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTodayAmount(todayAmount);

        // 总计统计
        vo.setTotalOrderCount(payOrderMapper.selectCount(baseQuery));
        LambdaQueryWrapper<PayOrder> successQuery = new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getMerchantId, merchantId)
                .eq(PayOrder::getStatus, Constants.OrderStatus.PAID);
        vo.setTotalSuccessCount(payOrderMapper.selectCount(successQuery));

        // 总金额
        List<PayOrder> allOrders = payOrderMapper.selectList(successQuery);
        BigDecimal totalAmount = allOrders.stream()
                .map(PayOrder::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalAmount(totalAmount);

        // 店铺、二维码数量
        vo.setShopCount(shopMapper.selectCount(new LambdaQueryWrapper<Shop>()
                .eq(Shop::getMerchantId, merchantId)));
        vo.setQrcodeCount(payQrcodeMapper.selectCount(new LambdaQueryWrapper<PayQrcode>()
                .eq(PayQrcode::getMerchantId, merchantId)));

        return vo;
    }

    @Override
    public void updateCallbackConfig(Long merchantId, String notifyUrl, String returnUrl) {
        Merchant merchant = this.getById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }

        merchant.setNotifyUrl(notifyUrl);
        merchant.setReturnUrl(returnUrl);
        this.updateById(merchant);
    }

    public static void main(String[] args) {
        System.out.println(SecureUtil.md5("nanyiba@fastpayQaz330600"));
    }
}
