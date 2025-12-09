package com.fastpay.controller.merchant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fastpay.common.PageResult;
import com.fastpay.common.Result;
import com.fastpay.dto.PayQrcodeDTO;
import com.fastpay.entity.PayQrcode;
import com.fastpay.service.PayQrcodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户平台 - 收款二维码管理控制器
 *
 * @author FastPay
 */
@Tag(name = "商户平台-二维码管理", description = "商户收款二维码管理操作")
@RestController
@RequestMapping("/api/merchant/qrcode")
public class MerchantQrcodeController {

    private final PayQrcodeService payQrcodeService;

    public MerchantQrcodeController(PayQrcodeService payQrcodeService) {
        this.payQrcodeService = payQrcodeService;
    }

    /**
     * 分页查询二维码列表
     */
    @Operation(summary = "分页查询二维码", description = "分页查询商户二维码列表")
    @GetMapping("/page")
    public Result<PageResult<PayQrcode>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) String payType,
            HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        Page<PayQrcode> page = payQrcodeService.pageQrcodes(new Page<>(current, size), merchantId, shopId, payType);
        PageResult<PayQrcode> result = new PageResult<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    /**
     * 获取店铺的二维码列表
     */
    @Operation(summary = "二维码列表", description = "获取店铺的二维码列表")
    @GetMapping("/list")
    public Result<List<PayQrcode>> list(@RequestParam Long shopId, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        List<PayQrcode> list = payQrcodeService.listByShop(shopId, merchantId);
        return Result.success(list);
    }

    /**
     * 添加收款二维码
     */
    @Operation(summary = "添加二维码", description = "添加收款二维码")
    @PostMapping
    public Result<PayQrcode> add(@Valid @RequestBody PayQrcodeDTO dto, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        PayQrcode qrcode = payQrcodeService.addQrcode(dto, merchantId);
        return Result.success("添加成功", qrcode);
    }

    /**
     * 更新收款二维码
     */
    @Operation(summary = "更新二维码", description = "更新收款二维码信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PayQrcodeDTO dto, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        dto.setId(id);
        payQrcodeService.updateQrcode(dto, merchantId);
        return Result.success("更新成功", null);
    }

    /**
     * 更新二维码状态
     */
    @Operation(summary = "更新状态", description = "更新二维码状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        payQrcodeService.updateStatus(id, status, merchantId);
        return Result.success("操作成功", null);
    }

    /**
     * 删除二维码
     */
    @Operation(summary = "删除二维码", description = "删除收款二维码")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        payQrcodeService.deleteQrcode(id, merchantId);
        return Result.success("删除成功", null);
    }
}
