package com.bi.barfdog.api.itemDto;

import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.ItemStatus;
import com.bi.barfdog.domain.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemSaveDto {

    @NotNull
    private ItemType itemType; // [RAW, TOPPING, GOODS]

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @PositiveOrZero
    private int originalPrice; // 원가

    @NotNull
    private DiscountType discountType; // [FIXED_RATE, FLAT_RATE]
    @PositiveOrZero
    private int discountDegree;
    @PositiveOrZero
    private int salePrice;

    @NotNull
    private boolean inStock;
    @PositiveOrZero
    private int remaining;

    @NotNull
    private String contents; // 상세내용
    @NotNull
    private boolean deliveryFree; // 배송비 무료 여부
    @NotNull
    private ItemStatus itemStatus; // 상품 노출여부

    @NotNull
    @Valid
    @Builder.Default
    private List<ItemOptionSaveDto> itemOptionSaveDtoList = new ArrayList<>();


    @Builder.Default
    private List<Long> contentImageIdList = new ArrayList<>();

    @Size(min = 1)
    @NotNull
    @Valid
    @Builder.Default
    private List<ItemImageOrderDto> itemImageOrderDtoList = new ArrayList<>();


    /*
    *  내부 클래스
    * */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemImageOrderDto {

        @NotNull
        private Long id;

        @NotNull
        @Positive
        private int leakOrder;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemOptionSaveDto {

        @NotEmpty
        private String name;

        @PositiveOrZero
        private int price;

        @PositiveOrZero
        private int remaining;

    }


}
