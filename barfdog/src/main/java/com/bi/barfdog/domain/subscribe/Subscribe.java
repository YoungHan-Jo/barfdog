package com.bi.barfdog.domain.subscribe;

import com.bi.barfdog.api.subscribeDto.UpdateGramDto;
import com.bi.barfdog.api.subscribeDto.UpdateSubscribeDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Card;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.Order;
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

    private LocalDateTime nextPaymentDate;
    private int nextPaymentPrice;
    private LocalDate nextDeliveryDate;

    private int skipCount;

    @Enumerated(EnumType.STRING)
    private SubscribeStatus status; // [BEFORE_PAYMENT, SUBSCRIBING, SUBSCRIBE_PENDING]

    @Builder.Default
    private boolean writeableReview = true; // status 가 SUBSCRIBING 이고 true 일 때 리뷰 가능

    @OneToOne(fetch = LAZY)
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


    private void setNextPaymentDate(LocalDateTime paymentDateNow) {
        if (plan == SubscribePlan.FULL) {
            nextPaymentDate = paymentDateNow.plusDays(14);
        } else if (plan == SubscribePlan.HALF || plan == SubscribePlan.TOPPING) {
            nextPaymentDate = paymentDateNow.plusDays(28);
        }
    }

    public void successPayment(Card card, Order order) {
        this.subscribeCount++;
        this.status = SubscribeStatus.SUBSCRIBING;
        this.card = card;
        this.nextDeliveryDate = calculateNextDeliveryDate();
        this.nextPaymentPrice = order.getOrderPrice();
        setNextPaymentDate(order.getPaymentDate());
    }

    private LocalDate calculateNextDeliveryDate() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int dayOfWeekNumber = dayOfWeek.getValue();
        int i = dayOfWeekNumber - 3;
        LocalDate nextDeliveryDate = null;
        if (dayOfWeekNumber <= 5) {
            nextDeliveryDate = today.minusDays(i+7);
        } else {
            nextDeliveryDate = today.minusDays(i+14);
        }
        return nextDeliveryDate;
    }

    public void failPayment() {
        status = SubscribeStatus.BEFORE_PAYMENT;
        nextDeliveryDate = null;
        nextPaymentDate = null;
    }

    public void useCoupon(MemberCoupon memberCoupon, int discount) {
        this.memberCoupon.cancel();
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
                memberCoupon.cancel();
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
}
