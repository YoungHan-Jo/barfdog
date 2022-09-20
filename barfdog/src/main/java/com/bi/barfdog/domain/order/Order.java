package com.bi.barfdog.domain.order;

import com.bi.barfdog.api.orderDto.SuccessGeneralRequestDto;
import com.bi.barfdog.api.orderDto.SuccessSubscribeRequestDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.subscribe.Subscribe;
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

    private String impUid; // 아임포트사 DB에 저장된 주문번호, 아임포트api 호출 시 사용가능

    private String merchantUid; // 주문 번호, 아임포트api 호출 시 사용가능

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 주문한 유저

    private int orderPrice; // 주문 가격(일반: 주문 총 가격/ 구독: 구독상품 원가)
    private int deliveryPrice; // 배송비
    private int discountTotal; // 할인 총 합계
    private int discountReward; // 적립금 할인
    private int discountCoupon; // 쿠폰 할인
    private int discountGrade; // 등급 할인

    private int paymentPrice; // 최종 결제 가격
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // [CREDIT_CARD, NAVER_PAY, KAKAO_PAY]
    private LocalDateTime paymentDate; // 결제 시간

    private LocalDateTime orderConfirmDate; // 주문 컨펌날짜

    private boolean isPackage; // 묶음배송여부

    private boolean isBrochure; // 브로슈어 받을지 여부
    private boolean isAgreePrivacy; // 연령 정책 동의 여부

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; // 주문:배송 - 다대일 관계 (묶음배송이면 하나의 배송에 여러개의 주문이 포함될 수 있음)

    public void changeSubscribe(Subscribe subscribe) {
        orderPrice = subscribe.getNextPaymentPrice();
        discountCoupon = subscribe.getDiscountCoupon();
        discountGrade = subscribe.getDiscountGrade();
        discountTotal = discountCoupon + discountGrade + discountReward;
        paymentPrice = orderPrice - discountTotal;
    }


    public void successGeneral(SuccessGeneralRequestDto requestDto) {
        this.impUid = requestDto.getImpUid();
        this.merchantUid = requestDto.getMerchantUid();
        this.orderStatus = OrderStatus.PAYMENT_DONE;
        this.paymentDate = LocalDateTime.now();
    }

    public void failPayment() {
        orderStatus = OrderStatus.FAILED;
        if (isPackage) {
            isPackage = false;
            delivery = null;
        } else {
            delivery.cancel();
        }
    }

    public void successSubscribe(SuccessSubscribeRequestDto requestDto) {
        impUid = requestDto.getImpUid();
        merchantUid = requestDto.getMerchantUid();
        orderStatus = OrderStatus.PAYMENT_DONE;
        paymentDate = LocalDateTime.now();
    }


    public void cancelRequest() {
        orderStatus = OrderStatus.CANCEL_REQUEST;
    }

    public void cancelOrderAndDelivery(OrderStatus status) {
        this.orderStatus = status;
        if (isPackage) {
            isPackage = false;
            delivery = null;
        } else {
            delivery.cancel();
        }
    }


    public void generalConfirm() {
        orderStatus = OrderStatus.CONFIRM;
        orderConfirmDate = LocalDateTime.now();
    }

    public void checkOrder() {
        this.orderStatus = OrderStatus.DELIVERY_READY;
    }

    public void checkSubscribeOrder() {
        this.orderStatus = OrderStatus.PRODUCING;
    }

    public void skipDelivery(LocalDate nextDeliveryDate) {
        delivery.skip(nextDeliveryDate);
    }

    public void startDelivery() {
        orderStatus = OrderStatus.DELIVERY_START;
    }

    public void failPaymentSchedule() {
        orderStatus = OrderStatus.FAILED;
    }

    public void successPaymentSchedule(String impUid) {
        this.impUid = impUid;
        orderStatus = OrderStatus.PAYMENT_DONE;
        delivery.paymentDone();
        paymentDate = LocalDateTime.now();
    }


    public void rejectCancelRequest(OrderStatus status) {
        orderStatus = status;
    }

    public void changeMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
    }


}
