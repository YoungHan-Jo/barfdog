package com.bi.barfdog.domain.subscribe;

import com.bi.barfdog.api.subscribeDto.UpdateGramDto;
import com.bi.barfdog.api.subscribeDto.UpdatePlanDto;
import com.bi.barfdog.api.subscribeDto.UpdateSubscribeDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Card;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import lombok.*;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Entity
public class Subscribe extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "subscribe_id")
    private Long id;

    @OneToOne(mappedBy = "subscribe")
    private Dog dog;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    private int subscribeCount;

    @Enumerated(EnumType.STRING)
    private SubscribePlan plan; // [FULL, HALF, TOPPING]

    @Builder.Default
    @OneToMany(mappedBy = "subscribe")
    private List<SubscribeRecipe> subscribeRecipes = new ArrayList<>();

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_coupon_id")
    private MemberCoupon memberCoupon;

    private int discount;

    private String nextOrderMerchantUid;

    private LocalDateTime nextPaymentDate;
    private int nextPaymentPrice; // 다음 회차 결제 금액(쿠폰적용/등급할인 전) -> 실제로는 nextpaymentprice->등급할인 적용 후 - discount 금액이 결제 됨
    private LocalDate nextDeliveryDate;

    private int skipCount;

    private String cancelReason; // 구독 취소 사유. , 콤마 기준으로 나열

    @Enumerated(EnumType.STRING)
    private SubscribeStatus status; // [BEFORE_PAYMENT, SUBSCRIBING, SUBSCRIBE_PENDING]

    @Builder.Default
    private boolean writeableReview = true; // status 가 SUBSCRIBING 이고 true 일 때 리뷰 가능

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "before_subscribe_id")
    private BeforeSubscribe beforeSubscribe;

    /*
    * 연관관계 편의 메서드
    * */
    public void addSubscribeRecipe(SubscribeRecipe subscribeRecipe) {
        subscribeRecipes.add(subscribeRecipe);
        subscribeRecipe.setSubscribe(this);
    }


    public void setDog(Dog dog) {
        this.dog = dog;
    }

    public void writeReview() {
        this.writeableReview = false;
    }

    public void update(UpdateSubscribeDto requestDto) {
        this.plan = requestDto.getPlan();
        this.nextPaymentPrice = requestDto.getNextPaymentPrice();
    }

    public void setBeforeSubscribe(BeforeSubscribe beforeSubscribe) {
        this.beforeSubscribe = beforeSubscribe;
    }


    private LocalDateTime calculateNextPaymentDate(LocalDateTime paymentDateNow) {
        LocalDateTime nextPaymentDate = null;
        if (plan == SubscribePlan.FULL) {
            nextPaymentDate = paymentDateNow.plusDays(14);
        } else if (plan == SubscribePlan.HALF || plan == SubscribePlan.TOPPING) {
            nextPaymentDate = paymentDateNow.plusDays(28);
        }
        return nextPaymentDate;
    }

    public void successPayment(Card card, Order order) {
        this.subscribeCount++;
        this.status = SubscribeStatus.SUBSCRIBING;
        this.card = card;
        this.nextDeliveryDate = calculateNextDeliveryDate();
        this.nextPaymentPrice = order.getOrderPrice();
        this.nextPaymentDate = calculateNextPaymentDate(order.getPaymentDate());
    }

    private LocalDate calculateNextDeliveryDate() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayOfWeekNumber = dayOfWeek.getValue();
        int i = dayOfWeekNumber - 3;
        LocalDate nextDeliveryDate = null;
        if (dayOfWeekNumber <= 5) {
            nextDeliveryDate = today.plusDays(i+7);
        } else {
            nextDeliveryDate = today.plusDays(i+14);
        }
        return nextDeliveryDate;
    }

    public void failPayment() {
        status = SubscribeStatus.BEFORE_PAYMENT;
        nextDeliveryDate = null;
        nextPaymentDate = null;
    }

    public void useCoupon(MemberCoupon memberCoupon, int discount) {
        if (this.memberCoupon != null) {
            this.memberCoupon.revival();
        }
        this.memberCoupon = memberCoupon;
        this.discount = discount;
        memberCoupon.useCoupon();
    }


    public void updateGram(UpdateGramDto requestDto) {
        int totalPrice = requestDto.getTotalPrice();
        this.nextPaymentPrice = totalPrice;
        dog.updateGram(requestDto.getGram());
        MemberCoupon memberCoupon = getMemberCoupon();
        int newDiscount = calculateNewDiscount(totalPrice, memberCoupon);
        this.discount = newDiscount;
    }

    private int calculateNewDiscount(int totalPrice, MemberCoupon memberCoupon) {
        int newDiscount = 0;
        if (memberCoupon != null) {
            Coupon coupon = memberCoupon.getCoupon();
            if (totalPrice < coupon.getAvailableMinPrice()) {
                memberCoupon.revival();
                this.memberCoupon = null;
            }else{
                if (coupon.getDiscountType() == DiscountType.FIXED_RATE) {
                    newDiscount = (int)Math.round(totalPrice * coupon.getDiscountDegree() / 100.0);
                    int availableMaxDiscount = coupon.getAvailableMaxDiscount();
                    if (newDiscount > availableMaxDiscount) {
                        newDiscount = availableMaxDiscount;
                    }
                } else {
                    newDiscount = coupon.getDiscountDegree();
                }
            }
        }
        return newDiscount;
    }

    public void updatePlan(UpdatePlanDto requestDto) {
        this.plan = requestDto.getPlan();
        int totalPrice = requestDto.getNextPaymentPrice();
        this.nextPaymentPrice = totalPrice;
        int newDiscount = calculateNewDiscount(totalPrice, memberCoupon);
        this.discount = newDiscount;
    }

    public void setNextOrderMerchantUid(String merchantUid) {
        this.nextOrderMerchantUid = merchantUid;
    }

    public LocalDate skipAndGetNextDeliveryDate(int count) {
        nextPaymentDate = nextPaymentDate.plusDays(count * 7);
        nextDeliveryDate = nextDeliveryDate.plusDays(count * 7);
        skipCount++;
        return nextDeliveryDate;
    }

    public void stopSubscribe(List<String> reasonList) {
        String reasons = getReasons(reasonList);
        this.cancelReason = reasons;
        this.discount = 0;
        this.nextOrderMerchantUid = null;
        this.nextPaymentDate = null;
        this.nextDeliveryDate = null;
        this.nextPaymentPrice = 0;
        this.status = SubscribeStatus.SUBSCRIBE_PENDING;
        this.skipCount = 0;
        if (this.memberCoupon != null) {
            memberCoupon.revival();
        }

    }

    private String getReasons(List<String> reasonList) {
        String reasons = "";
        if (reasonList.size() > 0) {
            reasons = reasonList.get(0);
            if (reasonList.size() > 1) {
                for (int i = 1; i < reasonList.size(); i++) {
                    reasons += "," + reasonList.get(i);
                }
            }
        }
        return reasons;
    }

    public void changeCard(Card card) {
        this.card = card;
    }

    public void successPaymentSchedule(SubscribeOrder order, String newMerchantUid) {
        subscribeCount++;
        memberCoupon = null;
        discount = 0;
        nextOrderMerchantUid = newMerchantUid;
        this.nextPaymentDate = calculateNextPaymentDate(order.getPaymentDate());
        nextPaymentPrice = order.getOrderPrice();
        nextDeliveryDate = calculateNextDeliveryDate();
        skipCount = 0;
    }
}
