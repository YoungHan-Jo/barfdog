package com.bi.barfdog.domain.subscribe;

import com.bi.barfdog.api.subscribeDto.UpdateGramDto;
import com.bi.barfdog.api.subscribeDto.UpdatePlanDto;
import com.bi.barfdog.api.subscribeDto.UpdateSubscribeDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Card;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import lombok.*;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
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
public class Subscribe extends BaseTimeEntity { // 구독, 다음번 결제 예정 정보

    @Id
    @GeneratedValue
    @Column(name = "subscribe_id")
    private Long id;

    @OneToOne(mappedBy = "subscribe")
    private Dog dog;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "card_id")
    private Card card; // 결제 카드

    private int subscribeCount; // 구독회차

    private BigDecimal oneMealRecommendGram; // 권장 한끼량

    @Enumerated(EnumType.STRING)
    private SubscribePlan plan; // [FULL, HALF, TOPPING]

    @Builder.Default
    @OneToMany(mappedBy = "subscribe") // 맵드바이, 조회용
    private List<SubscribeRecipe> subscribeRecipes = new ArrayList<>(); // 구독레시피 리스트

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_coupon_id")
    private MemberCoupon memberCoupon; // 다음번 결제에 사용할 쿠폰

    private int discountCoupon; // 쿠폰 할인량
    private int discountGrade; // 등급 할인량

    private String nextOrderMerchantUid; // 다음번 결제 주문uid

    private LocalDateTime nextPaymentDate; // 다음 결제일
    private int nextPaymentPrice; // 다음 회차 결제 금액(구독상품 원가) -> 실제로는 nextpaymentprice - (discountGrade + discountCoupon)금액이 결제 됨
    private LocalDate nextDeliveryDate; // 다음 배송일

    private int countSkipOneTime; // 한 회 건너뛰기 횟수
    private int countSkipOneWeek; // 한 주 건너뛰기 횟수

    private String cancelReason; // 구독 취소 사유/ (,)콤마 기준으로 나열

    @Enumerated(EnumType.STRING)
    private SubscribeStatus status; // [BEFORE_PAYMENT, SUBSCRIBING, SUBSCRIBE_PENDING]

    @Builder.Default
    private boolean writeableReview = true; // status 가 SUBSCRIBING 이고 true 일 때 리뷰 가능, 리뷰 한 번 쓰고나면 false로 전환되어 추가로 쓰기 불가


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

        this.oneMealRecommendGram = dog.getSurveyReport().getFoodAnalysis().getOneMealRecommendGram();
        this.plan = requestDto.getPlan();
        this.nextPaymentPrice = originalPrice;
        this.discountCoupon = calculateDiscountCoupon(originalPrice, memberCoupon);
        this.discountGrade = calculateDiscountGrade(originalPrice, member);
    }



    public void setBeforeSubscribe(BeforeSubscribe beforeSubscribe) {
        beforeSubscribe.setSubscribe(this);
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
        Delivery delivery = order.getDelivery();
        this.nextDeliveryDate = calculateNextDeliveryDate(delivery, this.plan);
        this.nextPaymentDate = calculateNextPaymentDate(order.getPaymentDate());
    }

    private LocalDate calculateNextDeliveryDate(Delivery delivery, SubscribePlan plan) {
        return delivery.getNextDeliveryDate().plusDays(plan == SubscribePlan.FULL ? 14 : 28);
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



    public void changeCoupon(MemberCoupon memberCoupon, int discountCoupon) {
        if (this.memberCoupon != null) {
            this.memberCoupon.revivalCoupon();
        }
        this.memberCoupon = memberCoupon;
        this.discountCoupon = discountCoupon;
        memberCoupon.useCoupon();
    }


    public void updateGram(UpdateGramDto requestDto) {
        int originalPrice = requestDto.getTotalPrice();
        MemberCoupon memberCoupon = getMemberCoupon();
        Member member = dog.getMember();

        int gram = requestDto.getGram();
//        dog.updateGram(gram);
        this.oneMealRecommendGram = BigDecimal.valueOf(gram);
        this.nextPaymentPrice = originalPrice;
        this.discountCoupon = calculateDiscountCoupon(originalPrice, memberCoupon);
        this.discountGrade = calculateDiscountGrade(originalPrice, member);
    }

    public void changeOneMealGram(BigDecimal oneMealRecommendGram) {
        this.oneMealRecommendGram = oneMealRecommendGram;
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

    public void skipSubscribe(String type) {

        if (type.equals("WEEK")) {
            nextPaymentDate = nextPaymentDate.plusDays(7);
            nextDeliveryDate = nextDeliveryDate.plusDays(7);
            countSkipOneWeek++;
        } else if (type.equals("ONCE")) {
            int days = plan == SubscribePlan.FULL ? 14 : 28;
            nextPaymentDate = nextPaymentDate.plusDays(days);
            nextDeliveryDate = nextDeliveryDate.plusDays(days);
            countSkipOneTime++;
        }
    }

    public void stopSubscribe(List<String> reasonList) {
        String reasons = getReasons(reasonList);
        this.cancelReason = reasons;
        this.status = SubscribeStatus.BEFORE_PAYMENT;
        this.nextOrderMerchantUid = null;
        this.nextPaymentDate = null;
        this.nextDeliveryDate = null;
        this.countSkipOneTime = 0;
        this.countSkipOneWeek = 0;
        this.nextPaymentPrice = 0;
        this.discountCoupon = 0;
        this.discountGrade = 0;
        if (this.memberCoupon != null) {
            memberCoupon.revivalCoupon();
            memberCoupon = null;
        }
    }

    public void cancelPayment() {
        if (this.subscribeCount > 0) {
            this.subscribeCount--;
        }
        this.status = SubscribeStatus.BEFORE_PAYMENT;
        this.card = null;
        this.nextDeliveryDate = null;
        this.nextPaymentDate = null;
        this.nextOrderMerchantUid = null;

        this.discountCoupon = 0;
        memberCoupon = null;
        this.countSkipOneTime = 0;
        this.countSkipOneWeek = 0;
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

    public void changeNextMerchantUid(String merchantUid) {
        this.nextOrderMerchantUid = merchantUid;
    }


}
