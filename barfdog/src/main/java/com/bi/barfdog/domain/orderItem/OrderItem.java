package com.bi.barfdog.domain.orderItem;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.GeneralOrder;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class OrderItem extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private GeneralOrder generalOrder;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    private int salePrice; // 판매가격
    private int amount; // 개수

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_coupon_id")
    private MemberCoupon memberCoupon; // 사용 쿠폰

    private int finalPrice;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

}
