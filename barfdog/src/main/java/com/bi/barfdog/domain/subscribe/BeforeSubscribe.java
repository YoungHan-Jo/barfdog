package com.bi.barfdog.domain.subscribe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
@Entity
public class BeforeSubscribe { // 변경 전 이전 구독 정보

    @Id @GeneratedValue
    @Column(name = "before_subscribe_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "subscribe_id")
    private Subscribe subscribe; // 현재 구독정보

    private int subscribeCount; // 몇번째 회차였는지

    @Enumerated(EnumType.STRING)
    private SubscribePlan plan; // [FULL, HALF, TOPPING]

    private BigDecimal oneMealRecommendGram; // 추천 한끼 그램

    private String recipeName; // [ , 로 구분]

    private int paymentPrice; // 주문 가격

    public void setSubscribe(Subscribe subscribe) {
        this.subscribe = subscribe;
    }
}
