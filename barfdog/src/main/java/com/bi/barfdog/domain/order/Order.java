package com.bi.barfdog.domain.order;

import com.bi.barfdog.api.orderDto.SuccessGeneralRequestDto;
import com.bi.barfdog.api.orderDto.SuccessSubscribeRequestDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.member.Member;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private String impUid; // 아임포트 결제번호

    private String merchantUid;

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
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // [CREDIT_CARD, NAVER_PAY, KAKAO_PAY]
    private LocalDateTime paymentDate; // 결제 시간

    private LocalDateTime orderConfirmDate;

    private boolean isPackage; // 묶음배송여부

    private boolean isBrochure;
    private boolean isAgreePrivacy;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;


    public void successGeneral(SuccessGeneralRequestDto requestDto) {
        this.impUid = requestDto.getImpUid();
        this.merchantUid = requestDto.getMerchantUid();
        this.orderStatus = OrderStatus.PAYMENT_DONE;
        this.paymentDate = LocalDateTime.now();
    }

    public void failGeneral() {
        orderStatus = OrderStatus.FAILED;
    }

    public void successSubscribe(SuccessSubscribeRequestDto requestDto) {
        impUid = requestDto.getImpUid();
        merchantUid = requestDto.getMerchantUid();
        orderStatus = OrderStatus.PAYMENT_DONE;
        paymentDate = LocalDateTime.now();
    }

    public void failSubscribe() {
        orderStatus = OrderStatus.FAILED;
    }

    public void cancelRequest() {
        orderStatus = OrderStatus.CANCEL_REQUEST;
    }

    public void generalConfirm() {
        orderStatus = OrderStatus.CONFIRM;
        orderConfirmDate = LocalDateTime.now();
    }

    public void orderConfirmGeneral() {
        this.orderStatus = OrderStatus.DELIVERY_READY;
    }

    public void orderConfirmSubscribe() {
        this.orderStatus = OrderStatus.PRODUCING;
    }

    public void confirmAs(OrderStatus status) {
        orderStatus = status;
    }

    public void skipDelivery(LocalDate nextDeliveryDate) {
        delivery.skip(nextDeliveryDate);
    }

    public void startDelivery() {
        orderStatus = OrderStatus.DELIVERY_START;
    }
}
