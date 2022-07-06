package com.bi.barfdog.domain.orderItem;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.GeneralOrder;
import com.bi.barfdog.domain.order.OrderStatus;
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

    private int discountAmount;

    private int finalPrice; // 옵션 포함 쿠폰적용 최종 가격

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int saveReward;
    private boolean isSavedReward;

    @Builder.Default
    private boolean writeableReview = true; //  true 일 때 리뷰 작성 가능

    public void writeReview() {
        this.writeableReview = false;
    }
}
