package com.fastpay.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fastpay.common.PageResult;
import com.fastpay.common.Result;
import com.fastpay.dto.PayQrcodeDTO;
import com.fastpay.entity.PayQrcode;
import com.fastpay.entity.Shop;
import com.fastpay.service.PayQrcodeService;
import com.fastpay.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 收款二维码管理控制器
 *
 * @author FastPay
 */
@Tag(name = "管理后台-二维码管理", description = "收款二维码管理操作")
@RestController
@RequestMapping("/api/admin/qrcode")
public class AdminQrcodeController {

    private final PayQrcodeService payQrcodeService;
    private final ShopService shopService;

    public AdminQrcodeController(PayQrcodeService payQrcodeService, ShopService shopService) {
        this.payQrcodeService = payQrcodeService;
        this.shopService = shopService;
    }

    /**
     * 分页查询二维码列表
     */
    @Operation(summary = "分页查询二维码", description = "分页查询二维码列表")
    @GetMapping("/page")
    public Result<PageResult<PayQrcode>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) String payType) {
        Page<PayQrcode> page = payQrcodeService.pageQrcodes(new Page<>(current, size), merchantId, shopId, payType);
        PageResult<PayQrcode> result = new PageResult<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    /**
     * 获取店铺的二维码列表
     */
    @Operation(summary = "二维码列表", description = "获取店铺的二维码列表")
    @GetMapping("/list")
    public Result<List<PayQrcode>> list(@RequestParam Long shopId) {
        List<PayQrcode> list = payQrcodeService.listByShop(shopId, null);
        return Result.success(list);
    }

    /**
     * 创建二维码
     */
    @Operation(summary = "创建二维码", description = "创建收款二维码")
    @PostMapping
    public Result<PayQrcode> create(@Valid @RequestBody PayQrcodeDTO dto) {
        // 获取店铺对应的商户ID
        Shop shop = shopService.getById(dto.getShopId());
        if (shop == null) {
            return Result.error("店铺不存在");
        }
        PayQrcode qrcode = payQrcodeService.addQrcode(dto, shop.getMerchantId());
        return Result.success("创建成功", qrcode);
    }

    /**
     * 更新二维码
     */
    @Operation(summary = "更新二维码", description = "更新收款二维码")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PayQrcodeDTO dto) {
        dto.setId(id);
        PayQrcode qrcode = payQrcodeService.getById(id);
        if (qrcode == null) {
            return Result.error("二维码不存在");
        }
        payQrcodeService.updateQrcode(dto, qrcode.getMerchantId());
        return Result.success("更新成功", null);
    }

    /**
     * 删除二维码
     */
    @Operation(summary = "删除二维码", description = "删除收款二维码")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        PayQrcode qrcode = payQrcodeService.getById(id);
        if (qrcode == null) {
            return Result.error("二维码不存在");
        }
        payQrcodeService.deleteQrcode(id, qrcode.getMerchantId());
        return Result.success("删除成功", null);
    }

    /**
     * 更新二维码状态
     */
    @Operation(summary = "更新状态", description = "更新二维码状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        PayQrcode qrcode = payQrcodeService.getById(id);
        if (qrcode == null) {
            return Result.error("二维码不存在");
        }
        payQrcodeService.updateStatus(id, status, qrcode.getMerchantId());
        return Result.success("操作成功", null);
    }
}
