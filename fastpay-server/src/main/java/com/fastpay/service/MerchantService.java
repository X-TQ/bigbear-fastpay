package com.fastpay.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fastpay.dto.LoginDTO;
import com.fastpay.dto.MerchantDTO;
import com.fastpay.entity.Merchant;
import com.fastpay.vo.DashboardVO;
import com.fastpay.vo.LoginVO;

import java.util.List;

/**
 * 商户服务接口
 *
 * @author FastPay
 */
public interface MerchantService extends IService<Merchant> {

    /**
     * 商户登录
     *
     * @param dto 登录信息
     * @param ip  客户端IP
     * @return 登录结果
     */
    LoginVO login(LoginDTO dto, String ip);

    /**
     * 创建商户（管理员操作）
     *
     * @param dto 商户信息
     * @return 创建的商户
     */
    Merchant createMerchant(MerchantDTO dto);

    /**
     * 更新商户信息
     *
     * @param dto 商户信息
     */
    void updateMerchant(MerchantDTO dto);

    /**
     * 更新商户状态
     *
     * @param id     商户ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 分页查询商户列表
     *
     * @param page         分页参数
     * @param merchantName 商户名称（模糊查询）
     * @param status       状态
     * @return 分页结果
     */
    Page<Merchant> pageMerchants(Page<Merchant> page, String merchantName, Integer status);

    /**
     * 获取所有商户列表（下拉选择用）
     *
     * @return 商户列表
     */
    List<Merchant> listAllMerchants();

    /**
     * 重置商户API密钥
     *
     * @param id 商户ID
     * @return 新的API密钥信息
     */
    Merchant resetApiKey(Long id);

    /**
     * 获取商户控制台统计数据
     *
     * @param merchantId 商户ID
     * @return 统计数据
     */
    DashboardVO getMerchantDashboard(Long merchantId);

    /**
     * 更新商户回调配置
     *
     * @param merchantId 商户ID
     * @param notifyUrl  回调地址
     * @param returnUrl  跳转地址
     */
    void updateCallbackConfig(Long merchantId, String notifyUrl, String returnUrl);
}
