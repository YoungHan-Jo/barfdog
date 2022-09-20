package com.bi.barfdog.domain.member;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Builder
@Entity
public class Card { // 신용카드

    @Id
    @GeneratedValue
    @Column(name = "card_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 카드:회원 - 다대일 관계

    private String customerUid; // 아임포트 결제 uid

    private String cardName;

    private String cardNumber;

}
