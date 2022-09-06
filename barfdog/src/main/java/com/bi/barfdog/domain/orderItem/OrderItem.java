package com.bi.barfdog.domain.orderItem;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.GeneralOrder;
import com.bi.barfdog.domain.order.OrderStatus;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_coupon_id")
    private MemberCoupon memberCoupon; // 사용 쿠폰

    private int discountAmount;

    private int finalPrice; // 옵션 포함 쿠폰적용 최종 가격

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int cancelReward; // 취소 반품으로 돌려받는 적립금 (주문단위 취소 시 알수 없음)
    private int cancelPrice; // 취소 반품으로 돌려받는 금액 (주문단위 취소 시 알수 없음)

    @Embedded
    private OrderCancel orderCancel;

    @Embedded
    private OrderReturn orderReturn;

    @Embedded
    private OrderExchange orderExchange;

    private int saveReward;
    private boolean isSavedReward;

    @Builder.Default
    private boolean writeableReview = true; //  true이고 상태 confirm 일 때 리뷰 작성 가능

    public void writeReview() {
        this.writeableReview = false;
    }

    public void successPayment() {
        status = OrderStatus.PAYMENT_DONE;
        writeableReview = true;
    }

    public void failPayment() {
        status = OrderStatus.FAILED;
        item.increaseRemaining(amount);
        revivalCoupon();
    }

    public void cancelPayment() {
        status = OrderStatus.CANCEL_PAYMENT;
        item.increaseRemaining(amount);
        revivalCoupon();
    }

    public void cancelRequestDate() {
        status = OrderStatus.CANCEL_REQUEST;
        orderCancel = OrderCancel.builder()
                .cancelRequestDate(LocalDateTime.now())
                .build();
    }

    public void confirm() {
        status = OrderStatus.CONFIRM;
        isSavedReward = true;
    }

    public void returnRequest(OrderReturn orderReturn) {
        this.orderReturn = orderReturn;
        this.status = OrderStatus.RETURN_REQUEST;
    }

    public void exchangeRequest(OrderExchange orderExchange) {
        this.orderExchange = orderExchange;
        this.status = OrderStatus.EXCHANGE_REQUEST;
    }

    public void checkGeneralOrder() {
        this.status = OrderStatus.DELIVERY_READY;
        this.generalOrder.checkOrder();
    }


    public void sellingCancel(OrderStatus status, String reason, String detailReason) {
        this.status = status;
        orderCancel = OrderCancel.builder()
                .cancelReason(reason)
                .cancelDetailReason(detailReason)
                .cancelRequestDate(orderCancel != null ? orderCancel.getCancelRequestDate() : null)
                .cancelConfirmDate(LocalDateTime.now())
                .build();
    }


    // 주문 단위 취소
    public void cancelOrderConfirmAndRevivalCoupon(OrderStatus status, String reason, String detailReason) {
        this.status = status;

        LocalDateTime now = LocalDateTime.now();
        orderCancel = OrderCancel.builder()
                .cancelReason(reason)
                .cancelDetailReason(detailReason)
                .cancelRequestDate(orderCancel != null ? orderCancel.getCancelRequestDate() : now)
                .cancelConfirmDate(now)
                .build();

        revivalCoupon();
    }


    public void exchangeConfirm(OrderStatus status) {
        this.status = status;
        orderExchange = OrderExchange.builder()
                .exchangeReason(orderExchange.getExchangeReason())
                .exchangeDetailReason(orderExchange.getExchangeDetailReason())
                .exchangeRequestDate(orderExchange.getExchangeRequestDate())
                .exchangeConfirmDate(LocalDateTime.now())
                .build();
    }

    private void revivalCoupon() {
        if (memberCoupon != null) {
            memberCoupon.revivalCoupon();
        }
    }


    public void returnConfirm(OrderStatus status) {
        this.status = status;
        orderReturn = OrderReturn.builder()
                .returnReason(orderReturn.getReturnReason())
                .returnDetailReason(orderReturn.getReturnDetailReason())
                .returnRequestDate(orderReturn.getReturnRequestDate())
                .returnConfirmDate(LocalDateTime.now())
                .build();

        revivalCoupon();
    }

    public void startDelivery() {
        if (status == OrderStatus.PRODUCING || status == OrderStatus.DELIVERY_READY) {
            status = OrderStatus.DELIVERY_START;
        }
    }


    public void rejectCancelRequest() {
        status = OrderStatus.DELIVERY_READY;
    }

    public void denyRequest() {
        status = this.generalOrder.getOrderStatus();
    }
}
