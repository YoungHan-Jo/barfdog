package com.bi.barfdog.domain.order;

import com.bi.barfdog.api.orderDto.OrderCancelRequestDto;
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
public class SubscribeOrder extends Order{ // Order를 상속받은 구독주문

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subscribe_id")
    private Subscribe subscribe; // 해당 구독

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_coupon_id")
    private MemberCoupon memberCoupon; // 사용한 쿠폰

    private int subscribeCount; // 해당 구독 주문이 몇번째 구독회차인지

    @Embedded
    private OrderCancel orderCancel; // 주문취소 관련 임베디드

    private int saveReward; // 구매확정시 받을 적립예정금
    private boolean isSavedReward; // 구매확정으로 적립받았는지 여부

    @Builder
    public SubscribeOrder(Long id, String impUid, String merchantUid, OrderStatus orderStatus, Member member, int orderPrice, int deliveryPrice, int discountTotal, int discountReward, int discountCoupon, int discountGrade, int paymentPrice, PaymentMethod paymentMethod, LocalDateTime paymentDate, LocalDateTime orderConfirmDate, boolean isPackage, boolean isBrochure, boolean isAgreePrivacy, Delivery delivery, Subscribe subscribe, MemberCoupon memberCoupon, int subscribeCount, OrderCancel orderCancel, int saveReward, boolean isSavedReward) {
        super(id, impUid, merchantUid, orderStatus, member, orderPrice, deliveryPrice, discountTotal, discountReward, discountCoupon, discountGrade, paymentPrice, paymentMethod, paymentDate, orderConfirmDate, isPackage, isBrochure, isAgreePrivacy, delivery);
        this.subscribe = subscribe;
        this.memberCoupon = memberCoupon;
        this.subscribeCount = subscribeCount;
        this.orderCancel = orderCancel;
        this.saveReward = saveReward;
        this.isSavedReward = isSavedReward;
    }




    public void setSubscribe(Subscribe subscribe) {
        this.subscribe = subscribe;
    }

    public void setCancelOrderInfo(String reason, String detailReason) {
        LocalDateTime now = LocalDateTime.now();

        orderCancel = OrderCancel.builder()
                .cancelReason(orderCancel != null ? orderCancel.getCancelReason() : reason)
                .cancelDetailReason(orderCancel != null ? orderCancel.getCancelDetailReason() : detailReason)
                .cancelRequestDate(orderCancel != null ? orderCancel.getCancelRequestDate() : now)
                .cancelConfirmDate(now)
                .build();
    }

    public void cancelReason(String reason, String detailReason) {
        orderCancel = OrderCancel.builder()
                .cancelReason(reason)
                .cancelDetailReason(detailReason)
                .cancelRequestDate(orderCancel != null ? orderCancel.getCancelRequestDate() : null)
                .cancelConfirmDate(LocalDateTime.now())
                .build();
    }

    public void setCancelConfirmDate() {
        orderCancel = OrderCancel.builder()
                .cancelReason(orderCancel != null ? orderCancel.getCancelReason() : null)
                .cancelDetailReason(orderCancel != null ? orderCancel.getCancelDetailReason() : null)
                .cancelRequestDate(orderCancel != null ? orderCancel.getCancelRequestDate() : null)
                .cancelConfirmDate(LocalDateTime.now())
                .build();
    }

    public void setCancelRequestDate(OrderCancelRequestDto requestDto) {
        orderCancel = OrderCancel.builder()
                .cancelReason(requestDto.getReason())
                .cancelDetailReason(requestDto.getDetailReason())
                .cancelRequestDate(LocalDateTime.now())
                .build();
    }

    public void subscribePaymentCancel() {
        LocalDateTime now = LocalDateTime.now();
        orderCancel = OrderCancel.builder()
                .cancelConfirmDate(now)
                .cancelRequestDate(now)
                .build();
    }

    public void changeCoupon(Subscribe subscribe) {
        this.memberCoupon = subscribe.getMemberCoupon();
    }

    public void giveExpectedRewards() {
        isSavedReward = true;
    }
}
