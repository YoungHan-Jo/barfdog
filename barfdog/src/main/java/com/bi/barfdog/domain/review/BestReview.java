package com.bi.barfdog.domain.review;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class BestReview { // 베스트 리뷰

    @Id @GeneratedValue
    @Column(name = "best_review_id")
    private Long id;

    private int leakedOrder; // 노출 순서

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review; // 베스트 리뷰: 리뷰 - 일대일


    public void changeLeakedOrder(int leakedOrder) {
        this.leakedOrder = leakedOrder;
    }
}
