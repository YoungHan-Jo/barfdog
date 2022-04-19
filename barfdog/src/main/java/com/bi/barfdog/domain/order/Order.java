package com.bi.barfdog.domain.order;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.member.Member;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@Entity
public abstract class Order extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    private String orderNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int orderPrice;
    private int deliveryPrice;
    private int discountTotal;
    private int discountReward;
    private int discountCoupon;

    private int paymentPrice;

    private int saveReward;
    private boolean isSavedReward;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // [CREDIT_CARD, NAVER_PAY, KAKAO_PAY]

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;


}
