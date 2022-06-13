package com.bi.barfdog.domain.subscribe;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.memberCoupon.MemberCoupon;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
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
    @JoinColumn(name = "order_id")
    private SubscribeOrder subscribeOrder;

    @Enumerated(EnumType.STRING)
    private SubscribePlan plan; // [FULL, HALF, TOPPING]

    @Builder.Default
    @OneToMany(mappedBy = "subscribe")
    private List<SubscribeRecipe> subscribeRecipes = new ArrayList<>();

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_coupon_id")
    private MemberCoupon memberCoupon;

    private int discount;

    private LocalDate nextPaymentDate;
    private int nextPaymentPrice;
    private LocalDate nextDeliveryDate;

    @Enumerated(EnumType.STRING)
    private SubscribeStatus status; // [BEFORE_PAYMENT, SUBSCRIBING, SUBSCRIBE_PENDING]

    @Builder.Default
    private boolean writeableReview = true; // status 가 SUBSCRIBING 이고 true 일 때 리뷰 가능

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

    public void setOrder(SubscribeOrder subscribeOrder) {
        this.subscribeOrder = subscribeOrder;
        this.subscribeOrder.setSubscribe(this);
    }

    public void writeReview() {
        this.writeableReview = false;
    }
}
