package com.fastpay.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fastpay.common.PageResult;
import com.fastpay.common.Result;
import com.fastpay.dto.ShopDTO;
import com.fastpay.entity.Shop;
import com.fastpay.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 店铺管理控制器
 *
 * @author FastPay
 */
@Tag(name = "管理后台-店铺管理", description = "店铺的增删改查等操作")
@RestController
@RequestMapping("/api/admin/shop")
public class AdminShopController {

    private final ShopService shopService;

    public AdminShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * 分页查询店铺列表
     */
    @Operation(summary = "分页查询店铺", description = "分页查询店铺列表")
    @GetMapping("/page")
    public Result<PageResult<Shop>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String shopName) {
        Page<Shop> page = shopService.pageShops(new Page<>(current, size), merchantId, shopName);
        PageResult<Shop> result = new PageResult<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return Result.success(result);
    }

    /**
     * 获取商户的店铺列表
     */
    @Operation(summary = "店铺列表", description = "获取商户的店铺列表，不传merchantId则返回所有店铺")
    @GetMapping("/list")
    public Result<List<Shop>> list(@RequestParam(required = false) Long merchantId) {
        List<Shop> list = shopService.listByMerchant(merchantId);
        return Result.success(list);
    }

    /**
     * 获取店铺详情
     */
    @Operation(summary = "店铺详情", description = "根据ID获取店铺详情")
    @GetMapping("/{id}")
    public Result<Shop> getById(@PathVariable Long id) {
        Shop shop = shopService.getShopDetail(id, null);
        return Result.success(shop);
    }

    /**
     * 创建店铺
     */
    @Operation(summary = "创建店铺", description = "创建新店铺")
    @PostMapping
    public Result<Shop> create(@Valid @RequestBody ShopDTO dto) {
        Shop shop = shopService.createShop(dto, null);
        return Result.success("创建成功", shop);
    }

    /**
     * 更新店铺
     */
    @Operation(summary = "更新店铺", description = "更新店铺信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ShopDTO dto) {
        dto.setId(id);
        shopService.updateShop(dto, null);
        return Result.success("更新成功", null);
    }

    /**
     * 更新店铺状态
     */
    @Operation(summary = "更新状态", description = "更新店铺状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        shopService.updateStatus(id, status, null);
        return Result.success("操作成功", null);
    }

    /**
     * 删除店铺
     */
    @Operation(summary = "删除店铺", description = "删除店铺")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        shopService.deleteShop(id, null);
        return Result.success("删除成功", null);
    }
}
