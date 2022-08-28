package com.bi.barfdog.domain.subscribe;

import com.bi.barfdog.api.subscribeDto.UpdateGramDto;
import com.bi.barfdog.api.subscribeDto.UpdatePlanDto;
import com.bi.barfdog.api.subscribeDto.UpdateSubscribeDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Card;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
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

    private int discountCoupon;
    private int discountGrade;

    private String nextOrderMerchantUid; // 다음번 결제 주문uid

    private LocalDateTime nextPaymentDate; // 다음 결제일
    private int nextPaymentPrice; // 다음 회차 결제 금액(구독상품 원가) -> 실제로는 nextpaymentprice - (discountGrade + discountCoupon)금액이 결제 됨
    private LocalDate nextDeliveryDate;

    private int countSkipOneTime;
    private int countSkipOneWeek;

    private String cancelReason; // 구독 취소 사유/ (,)콤마 기준으로 나열

    @Enumerated(EnumType.STRING)
    private SubscribeStatus status; // [BEFORE_PAYMENT, SUBSCRIBING, SUBSCRIBE_PENDING]

    @Builder.Default
    private boolean writeableReview = true; // status 가 SUBSCRIBING 이고 true 일 때 리뷰 가능, 리뷰 한 번 쓰고나면 false로 전환되어 추가로 쓰기 불가

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

    public void updatePlan(UpdatePlanDto requestDto) {
        int originalPrice = requestDto.getNextPaymentPrice();
        Member member = dog.getMember();

        this.plan = requestDto.getPlan();
        this.nextPaymentPrice = originalPrice;
        this.discountCoupon = calculateDiscountCoupon(originalPrice, memberCoupon);
        this.discountGrade = calculateDiscountGrade(originalPrice, member);
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
            this.memberCoupon.revivalCoupon();
        }
        this.memberCoupon = memberCoupon;
        this.discountCoupon = discount;
        memberCoupon.useCoupon();
    }


    public void updateGram(UpdateGramDto requestDto) {
        int originalPrice = requestDto.getTotalPrice();
        MemberCoupon memberCoupon = getMemberCoupon();
        Member member = dog.getMember();

        dog.updateGram(requestDto.getGram());
        this.nextPaymentPrice = originalPrice;
        this.discountCoupon = calculateDiscountCoupon(originalPrice, memberCoupon);
        this.discountGrade = calculateDiscountGrade(originalPrice, member);
    }

    private int calculateDiscountCoupon(int originalPrice, MemberCoupon memberCoupon) {
        int discountCoupon = 0;
        if (memberCoupon != null) {
            Coupon coupon = memberCoupon.getCoupon();
            if (originalPrice < coupon.getAvailableMinPrice()) {
                memberCoupon.revivalCoupon();
                this.memberCoupon = null;
            }else{
                if (coupon.getDiscountType() == DiscountType.FIXED_RATE) {
                    discountCoupon = (int)Math.round(originalPrice * coupon.getDiscountDegree() / 100.0);
                    int availableMaxDiscount = coupon.getAvailableMaxDiscount();
                    if (discountCoupon > availableMaxDiscount) {
                        discountCoupon = availableMaxDiscount;
                    }
                } else {
                    discountCoupon = coupon.getDiscountDegree();
                }
            }
        }
        return discountCoupon;
    }

    private int calculateDiscountGrade(int originalPrice, Member member) {
        int discountGrade = 0;
        double percent;
        Grade grade = member.getGrade();
        switch (grade) {
            case 골드: percent = 1.0;
                break;
            case 플래티넘: percent = 3.0;
                break;
            case 다이아몬드: percent = 5.0;
                break;
            case 더바프: percent = 7.0;
                break;
            default: percent = 0.0;
                break;
        }

        if (percent > 0.0) {
            discountGrade = (int) Math.round(originalPrice * percent / 100.0);
        }

        return discountGrade;
    }



    public void setNextOrderMerchantUid(String merchantUid) {
        this.nextOrderMerchantUid = merchantUid;
    }

    public LocalDate skipAndGetNextDeliveryDate(int count) {
        nextPaymentDate = nextPaymentDate.plusDays(count * 7);
        nextDeliveryDate = nextDeliveryDate.plusDays(count * 7);
        countSkipOneTime++;
        return nextDeliveryDate;
    }

    public void stopSubscribe(List<String> reasonList) {
        String reasons = getReasons(reasonList);
        this.cancelReason = reasons;
        this.discountCoupon = 0;
        this.nextOrderMerchantUid = null;
        this.nextPaymentDate = null;
        this.nextDeliveryDate = null;
        this.nextPaymentPrice = 0;
        this.status = SubscribeStatus.SUBSCRIBE_PENDING;
        this.countSkipOneTime = 0;
        this.countSkipOneWeek = 0;
        if (this.memberCoupon != null) {
            memberCoupon.revivalCoupon();
        }

    }

    public void cancelPayment() {
        this.discountCoupon = 0;
        if (this.subscribeCount > 0) {
            this.subscribeCount--;
        }
        this.nextPaymentDate = null;
        this.nextDeliveryDate = null;
        this.nextPaymentPrice = 0;
        this.status = SubscribeStatus.SUBSCRIBE_PENDING;
        this.countSkipOneTime = 0;
        this.countSkipOneWeek = 0;
        if (this.memberCoupon != null) {
            memberCoupon.revivalCoupon();
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
        discountCoupon = 0;
        nextOrderMerchantUid = newMerchantUid;
        this.nextPaymentDate = calculateNextPaymentDate(order.getPaymentDate());
        nextPaymentPrice = order.getOrderPrice();
        nextDeliveryDate = calculateNextDeliveryDate();
        this.countSkipOneTime = 0;
        this.countSkipOneWeek = 0;
    }
}
