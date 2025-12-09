package com.fastpay.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fastpay.common.PageResult;
import com.fastpay.common.Result;
import com.fastpay.dto.MerchantChannelDTO;
import com.fastpay.entity.Merchant;
import com.fastpay.entity.MerchantChannel;
import com.fastpay.service.MerchantChannelService;
import com.fastpay.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 通道管理控制器
 *
 * @author FastPay
 */
@Tag(name = "管理后台-通道管理", description = "通道的增删改查等操作")
@RestController
@RequestMapping("/api/admin/channel")
public class AdminChannelController {

    private final MerchantChannelService merchantChannelService;
    private final MerchantService merchantService;

    public AdminChannelController(MerchantChannelService merchantChannelService, MerchantService merchantService) {
        this.merchantChannelService = merchantChannelService;
        this.merchantService = merchantService;
    }

    /**
     * 分页查询通道列表
     */
    @Operation(summary = "分页查询通道", description = "分页查询通道列表")
    @GetMapping("/page")
    public Result<PageResult<MerchantChannel>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String payType,
            @RequestParam(required = false) Integer status) {
        
        Page<MerchantChannel> page = new Page<>(current, size);
        LambdaQueryWrapper<MerchantChannel> wrapper = new LambdaQueryWrapper<>();
        
        if (merchantId != null) {
            wrapper.eq(MerchantChannel::getMerchantId, merchantId);
        }
        if (StringUtils.hasText(payType)) {
            wrapper.eq(MerchantChannel::getPayType, payType);
        }
        if (status != null) {
            wrapper.eq(MerchantChannel::getStatus, status);
        }
        wrapper.orderByDesc(MerchantChannel::getCreateTime);
        
        Page<MerchantChannel> result = merchantChannelService.page(page, wrapper);
        
        // 填充商户名称
        result.getRecords().forEach(channel -> {
            Merchant merchant = merchantService.getById(channel.getMerchantId());
            if (merchant != null) {
                channel.setMerchantName(merchant.getMerchantName());
            }
        });
        
        PageResult<MerchantChannel> pageResult = new PageResult<>(
            result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()
        );
        return Result.success(pageResult);
    }

    /**
     * 获取通道列表
     */
    @Operation(summary = "通道列表", description = "获取通道列表")
    @GetMapping("/list")
    public Result<List<MerchantChannel>> list(@RequestParam(required = false) Long merchantId) {
        LambdaQueryWrapper<MerchantChannel> wrapper = new LambdaQueryWrapper<>();
        if (merchantId != null) {
            wrapper.eq(MerchantChannel::getMerchantId, merchantId);
        }
        wrapper.orderByDesc(MerchantChannel::getCreateTime);
        List<MerchantChannel> list = merchantChannelService.list(wrapper);
        
        // 填充商户名称
        list.forEach(channel -> {
            Merchant merchant = merchantService.getById(channel.getMerchantId());
            if (merchant != null) {
                channel.setMerchantName(merchant.getMerchantName());
            }
        });
        
        return Result.success(list);
    }

    /**
     * 创建通道
     */
    @Operation(summary = "创建通道", description = "创建新通道")
    @PostMapping
    public Result<MerchantChannel> create(@Valid @RequestBody MerchantChannelDTO dto) {
        MerchantChannel channel = merchantChannelService.addChannel(dto, dto.getMerchantId());
        return Result.success("创建成功", channel);
    }

    /**
     * 更新通道
     */
    @Operation(summary = "更新通道", description = "更新通道信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MerchantChannelDTO dto) {
        dto.setId(id);
        // 获取原通道信息
        MerchantChannel channel = merchantChannelService.getById(id);
        if (channel == null) {
            return Result.error("通道不存在");
        }
        merchantChannelService.updateChannel(dto, channel.getMerchantId());
        return Result.success("更新成功", null);
    }

    /**
     * 更新通道状态
     */
    @Operation(summary = "更新状态", description = "启用或禁用通道")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        MerchantChannel channel = merchantChannelService.getById(id);
        if (channel == null) {
            return Result.error("通道不存在");
        }
        merchantChannelService.updateStatus(id, status, channel.getMerchantId());
        return Result.success("操作成功", null);
    }

    /**
     * 删除通道
     */
    @Operation(summary = "删除通道", description = "删除通道")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        MerchantChannel channel = merchantChannelService.getById(id);
        if (channel == null) {
            return Result.error("通道不存在");
        }
        merchantChannelService.deleteChannel(id, channel.getMerchantId());
        return Result.success("删除成功", null);
    }
}
