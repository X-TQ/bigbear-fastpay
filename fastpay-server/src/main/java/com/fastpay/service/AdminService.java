package com.fastpay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fastpay.dto.LoginDTO;
import com.fastpay.entity.Admin;
import com.fastpay.vo.DashboardVO;
import com.fastpay.vo.LoginVO;

/**
 * 管理员服务接口
 *
 * @author FastPay
 */
public interface AdminService extends IService<Admin> {

    /**
     * 管理员登录
     *
     * @param dto 登录信息
     * @param ip  客户端IP
     * @return 登录结果
     */
    LoginVO login(LoginDTO dto, String ip);

    /**
     * 获取控制台统计数据
     *
     * @return 统计数据
     */
    DashboardVO getDashboard();

    /**
     * 初始化默认管理员账号
     */
    void initDefaultAdmin();
}
