package com.bi.barfdog.domain.order;

import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.orderItem.OrderCancel;
import com.bi.barfdog.domain.subscribe.Subscribe;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("subscribe")
@Entity
public class SubscribeOrder extends Order{

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subscribe_id")
    private Subscribe subscribe;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_coupon_id")
    private MemberCoupon memberCoupon;

    private int subscribeCount;

    private OrderCancel orderCancel;

    @Builder
    public SubscribeOrder(Long id, String impUid, String merchantUid, OrderStatus orderStatus, Member member, int orderPrice, int deliveryPrice, int discountTotal, int discountReward, int discountCoupon, int paymentPrice, PaymentMethod paymentMethod, LocalDateTime paymentDate, LocalDateTime orderConfirmDate, boolean isPackage, boolean isBrochure, boolean isAgreePrivacy, Delivery delivery, Subscribe subscribe, MemberCoupon memberCoupon, int subscribeCount, OrderCancel orderCancel) {
        super(id, impUid, merchantUid, orderStatus, member, orderPrice, deliveryPrice, discountTotal, discountReward, discountCoupon, paymentPrice, paymentMethod, paymentDate, orderConfirmDate, isPackage, isBrochure, isAgreePrivacy, delivery);
        this.subscribe = subscribe;
        this.memberCoupon = memberCoupon;
        this.subscribeCount = subscribeCount;
        this.orderCancel = orderCancel;
    }

    public void setSubscribe(Subscribe subscribe) {
        this.subscribe = subscribe;
    }

}
