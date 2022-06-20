package com.bi.barfdog.api.itemDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.coupon.DiscountType;
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
public class QueryItemDto {

    private ItemDto itemDto;

    private DeliveryCondDto deliveryCondDto;

    private List<ItemOptionDto> itemOptionDtoList = new ArrayList<>();

    private List<ItemImageDto> itemImageDtoList = new ArrayList<>();

    private ReviewDto reviewDto;

    @Data
    @AllArgsConstructor
    public static class ReviewDto {
        private double star;
        private Long count;
    }


    @Data
    @AllArgsConstructor
    public static class ItemImageDto{
        private Long id;
        private int leakedOrder;
        private String filename;
        private String url;

        public void changeUrl(String filename) {
            url = linkTo(InfoController.class).slash("display/items?filename=" + filename).toString();
        }
    }

    @Data
    @AllArgsConstructor
    public static class DeliveryCondDto {
        private int price; // 기본 배송비
        private int freeCondition; // xx 원 이상 무료 배송 조건
    }

    @Data
    @AllArgsConstructor
    public static class ItemOptionDto {
        private Long id;
        private String name;
        private int optionPrice;
        private int remaining;
    }


    @Data
    @AllArgsConstructor
    public static class ItemDto{
        private Long id;
        private String name;
        private String description;
        private int originalPrice; // 원가
        private DiscountType discountType; // [FIXED_RATE, FLAT_RATE]
        private int discountDegree;
        private int salePrice; // 판매가
        private boolean inStock;
        private int remaining;
        private int totalSalesAmount;
        private String contents; // 상세내용
        private String itemIcons; // 상품 아이콘 "BEST,NEW"
        private boolean deliveryFree; // 배송비 무료 여부
    }
}
