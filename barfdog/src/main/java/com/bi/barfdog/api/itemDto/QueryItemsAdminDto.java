package com.bi.barfdog.api.itemDto;

import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryItemsAdminDto {

    private Long id;

    private String name;

    private boolean option;

    private int originalPrice;

    private String discount;

    private int salePrice;

    private ItemStatus status; // [LEAKED,HIDDEN]

    private int remaining;

    private LocalDateTime createdDate;
}
