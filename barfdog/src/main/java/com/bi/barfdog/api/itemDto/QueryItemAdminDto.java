package com.bi.barfdog.api.itemDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.ItemStatus;
import com.bi.barfdog.domain.item.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryItemAdminDto {

    private ItemAdminDto itemAdminDto;

    @Builder.Default
    private List<ItemOptionAdminDto> itemOptionAdminDtoList = new ArrayList<>();

    @Builder.Default
    private List<ItemImageAdminDto> itemImageAdminDtoList = new ArrayList<>();

    @Builder.Default
    private List<ItemContentImageDto> itemContentImageDtoList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemAdminDto {
        private Long id;

        private ItemType itemType; // [RAW, TOPPING, GOODS]

        private String name;

        private String description;

        private int originalPrice;

        private DiscountType discountType; // [FIXED_RATE, FLAT_RATE]

        private int discountDegree;

        private int salePrice; // 판매가

        private boolean inStock;

        private int remaining;

        private String contents; // 상세내용

        private String itemIcons; // 상품 아이콘 "BEST,NEW"

        private boolean deliveryFree; // 배송비 무료 여부

        private ItemStatus status; // [LEAKED,HIDDEN]
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemOptionAdminDto {
        private Long id;
        private String name;
        private int optionPrice;
        private int remaining;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemImageAdminDto {
        private Long id;

        private int leakOrder;
        private String filename;
        private String url;

        public void changeUrl() {
            this.url = linkTo(InfoController.class).slash("display").slash("items?filename=" + filename).toString();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemContentImageDto {
        private Long id;

        private String filename;
        private String url;

        public void changeUrl() {
            this.url = linkTo(InfoController.class).slash("display").slash("items?filename=" + filename).toString();
        }
    }




}
