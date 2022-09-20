package com.bi.barfdog.domain.item;

import com.bi.barfdog.api.itemDto.ItemUpdateDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.coupon.DiscountType;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class Item extends BaseTimeEntity { // 일반 상품, 상품,

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ItemType itemType; // [RAW, TOPPING, GOODS]

    private String name;

    private String description;

    private int originalPrice; // 원가

    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // [FIXED_RATE, FLAT_RATE] 할인 타입

    private int discountDegree; // 할인 정도

    private int salePrice; // 할인 후 판매가격, originalPrice-totalSalesAmount

    private boolean inStock; // 재고 여부

    private int remaining; // 남은수량

    private int totalSalesAmount; // 할인 량

    @Column(columnDefinition = "TEXT")
    private String contents; // 상세내용

    private String itemIcons; // 상품 아이콘 "BEST,NEW"

    private boolean deliveryFree; // 배송비 무료 여부

    @Enumerated(EnumType.STRING)
    private ItemStatus status; // [LEAKED,HIDDEN]

    private boolean isDeleted; // 삭제된 아이템인지 여부

    public void update(ItemUpdateDto requestDto) {
        itemType = requestDto.getItemType();
        name = requestDto.getName();
        description = requestDto.getDescription();
        originalPrice = requestDto.getOriginalPrice();
        discountType = requestDto.getDiscountType();
        discountDegree = requestDto.getDiscountDegree();
        salePrice = requestDto.getSalePrice();
        inStock = requestDto.isInStock();
        remaining = requestDto.getRemaining();
        itemIcons = requestDto.getItemIcons();
        contents = requestDto.getContents();
        deliveryFree = requestDto.isDeliveryFree();
        status = requestDto.getItemStatus();
    }

    public void delete() {
        isDeleted = true;
    }

    public void increaseRemaining(int amount) {
        remaining += amount;
        if (remaining > 0) {
            inStock = true;
        }
    }

    public void decreaseRemaining(int amount) {
        remaining -= amount;
        if (remaining < 1) {
            remaining = 0;
            inStock = false;
        }
    }
}
