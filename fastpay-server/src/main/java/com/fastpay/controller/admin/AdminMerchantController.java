package com.fastpay.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fastpay.common.PageResult;
import com.fastpay.common.Result;
import com.fastpay.dto.MerchantDTO;
import com.fastpay.entity.Merchant;
import com.fastpay.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 商户管理控制器
 * 处理商户的增删改查等操作
 *
 * @author FastPay
 */
@Tag(name = "管理后台-商户管理", description = "商户的增删改查等操作")
@RestController
@RequestMapping("/api/admin/merchant")
public class AdminMerchantController {

    private final MerchantService merchantService;

    public AdminMerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    /**
     * 分页查询商户列表
     *
     * @param current      当前页
     * @param size         每页大小
     * @param merchantName 商户名称
     * @param status       状态
     * @return 分页结果
     */
    @Operation(summary = "分页查询商户", description = "分页查询商户列表")
    @GetMapping("/page")
    public Result<PageResult<Merchant>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String merchantName,
            @RequestParam(required = false) Integer status) {
        Page<Merchant> page = merchantService.pageMerchants(new Page<>(current, size), merchantName, status);
        PageResult<Merchant> result = new PageResult<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    /**
     * 获取所有商户列表（下拉选择用）
     *
     * @return 商户列表
     */
    @Operation(summary = "商户列表", description = "获取所有商户列表（下拉选择用）")
    @GetMapping("/list")
    public Result<List<Merchant>> list() {
        List<Merchant> list = merchantService.listAllMerchants();
        return Result.success(list);
    }

    /**
     * 获取商户详情
     *
     * @param id 商户ID
     * @return 商户信息
     */
    @Operation(summary = "商户详情", description = "根据ID获取商户详情")
    @GetMapping("/{id}")
    public Result<Merchant> getById(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        return Result.success(merchant);
    }

    /**
     * 创建商户
     *
     * @param dto 商户信息
     * @return 创建结果
     */
    @Operation(summary = "创建商户", description = "创建新商户")
    @PostMapping
    public Result<Merchant> create(@Valid @RequestBody MerchantDTO dto) {
        Merchant merchant = merchantService.createMerchant(dto);
        return Result.success("创建成功", merchant);
    }

    /**
     * 更新商户信息
     *
     * @param id  商户ID
     * @param dto 商户信息
     * @return 更新结果
     */
    @Operation(summary = "更新商户", description = "更新商户信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MerchantDTO dto) {
        dto.setId(id);
        merchantService.updateMerchant(dto);
        return Result.success("更新成功", null);
    }

    /**
     * 更新商户状态
     *
     * @param id     商户ID
     * @param status 状态
     * @return 更新结果
     */
    @Operation(summary = "更新状态", description = "更新商户状态（启用/禁用）")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        merchantService.updateStatus(id, status);
        return Result.success("操作成功", null);
    }

    /**
     * 重置商户API密钥
     *
     * @param id 商户ID
     * @return 新的密钥信息
     */
    @Operation(summary = "重置密钥", description = "重置商户API密钥")
    @PostMapping("/{id}/reset-key")
    public Result<Merchant> resetApiKey(@PathVariable Long id) {
        Merchant merchant = merchantService.resetApiKey(id);
        return Result.success("重置成功", merchant);
    }
}
