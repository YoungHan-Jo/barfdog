package com.bi.barfdog.domain.product;

import com.bi.barfdog.domain.coupon.DiscountType;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class Product {

    @Id @GeneratedValue
    @Column(name = "product_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    private String name;

    private String description;

    private int originalPrice; //판매가격

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    private int discountDegree;

    private int discountPrice; // 할인 가격

    private boolean inStock; // 재고 여부












}
