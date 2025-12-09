package com.fastpay.dto;

import com.fastpay.entity.Merchant;
import com.fastpay.entity.MerchantChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author nanyiba
 * @Date 2025
 * @Description
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BizCallbackDTO {

    private PayNotifyDTO payNotifyDTO;

    private Merchant merchant;

    private MerchantChannel merchantChannel;

}
