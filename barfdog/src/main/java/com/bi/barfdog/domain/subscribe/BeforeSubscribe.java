package com.bi.barfdog.domain.subscribe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
@Entity
public class BeforeSubscribe {

    @Id @GeneratedValue
    @Column(name = "befor_subscribe_id")
    private Long id;

    private int subscribeCount;

    @Enumerated(EnumType.STRING)
    private SubscribePlan plan; // [FULL, HALF, TOPPING]

    private BigDecimal oneMealRecommendGram;

    private String recipeName; // [ , 로 구분]

    private int orderPrice; // 주문 가격

}
