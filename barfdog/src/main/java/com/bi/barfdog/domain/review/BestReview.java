package com.bi.barfdog.domain.review;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class BestReview {

    @Id @GeneratedValue
    @Column(name = "best_review_id")
    private Long id;

    private int leakedOrder;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review;


    public void changeLeakedOrder(int leakedOrder) {
        this.leakedOrder = leakedOrder;
    }
}
