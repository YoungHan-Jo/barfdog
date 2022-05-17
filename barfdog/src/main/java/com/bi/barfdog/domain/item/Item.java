package com.bi.barfdog.domain.item;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.coupon.DiscountType;
import lombok.*;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class Item extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ItemType itemType; // [FRESH, TOPPING, GOODS]

    private String name;

    private String description;

    private int originalPrice; // 원가

    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // [FIXED_RATE, FLAT_RATE]
    private int discountDegree;
    private int salePrice; // 판매가

    private boolean inStock;

    private int remaining;

    @Column(columnDefinition = "TEXT")
    private String contents; // 상세내용

    private boolean deliveryFree; // 배송비 무료 여부

    @Enumerated(EnumType.STRING)
    private ItemStatus status; // [LEAKED,HIDDEN]

    @OneToMany(mappedBy = "item")
    private List<ItemImage> itemImageList = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    private List<ItemContentImage> contentImageList = new ArrayList<>();



}
