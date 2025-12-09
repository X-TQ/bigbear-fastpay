package com.fastpay.controller.merchant;

import com.fastpay.common.Result;
import com.fastpay.dto.MerchantChannelDTO;
import com.fastpay.entity.MerchantChannel;
import com.fastpay.service.MerchantChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户平台 - 通道管理控制器
 *
 * @author FastPay
 */
@Tag(name = "商户平台-通道管理", description = "商户通道配置管理")
@RestController
@RequestMapping("/api/merchant/channel")
public class MerchantChannelController {

    private final MerchantChannelService merchantChannelService;

    public MerchantChannelController(MerchantChannelService merchantChannelService) {
        this.merchantChannelService = merchantChannelService;
    }

    /**
     * 获取通道列表
     */
    @Operation(summary = "通道列表", description = "获取当前商户的通道列表")
    @GetMapping("/list")
    public Result<List<MerchantChannel>> list(HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        List<MerchantChannel> list = merchantChannelService.listByMerchant(merchantId);
        return Result.success(list);
    }

    /**
     * 获取指定支付类型的启用通道列表
     */
    @Operation(summary = "按支付类型获取通道", description = "获取指定支付类型的启用通道列表")
    @GetMapping("/list/{payType}")
    public Result<List<MerchantChannel>> listByPayType(@PathVariable String payType, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        List<MerchantChannel> list = merchantChannelService.listByMerchantAndPayType(merchantId, payType);
        return Result.success(list);
    }

    /**
     * 获取通道详情
     */
    @Operation(summary = "通道详情", description = "获取通道详情")
    @GetMapping("/{id}")
    public Result<MerchantChannel> detail(@PathVariable Long id, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        MerchantChannel channel = merchantChannelService.getByIdAndMerchant(id, merchantId);
        if (channel == null) {
            return Result.error("通道不存在");
        }
        return Result.success(channel);
    }

    /**
     * 添加通道
     */
    @Operation(summary = "添加通道", description = "添加新的通道配置")
    @PostMapping
    public Result<MerchantChannel> add(@Valid @RequestBody MerchantChannelDTO dto, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        MerchantChannel channel = merchantChannelService.addChannel(dto, merchantId);
        return Result.success("添加成功", channel);
    }

    /**
     * 更新通道
     */
    @Operation(summary = "更新通道", description = "更新通道配置")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MerchantChannelDTO dto, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        dto.setId(id);
        merchantChannelService.updateChannel(dto, merchantId);
        return Result.success("更新成功", null);
    }

    /**
     * 删除通道
     */
    @Operation(summary = "删除通道", description = "删除通道配置")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        merchantChannelService.deleteChannel(id, merchantId);
        return Result.success("删除成功", null);
    }

    /**
     * 更新通道状态
     */
    @Operation(summary = "更新状态", description = "启用或禁用通道")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        merchantChannelService.updateStatus(id, status, merchantId);
        return Result.success("操作成功", null);
    }

    /**
     * 生成默认模版
     */
    @Operation(summary = "生成模版", description = "生成默认的通道消息模版")
    @GetMapping("/{id}/template")
    public Result<String> generateTemplate(@PathVariable Long id) {
        String template = merchantChannelService.generateDefaultTemplate(id);
        return Result.success(template);
    }
}
