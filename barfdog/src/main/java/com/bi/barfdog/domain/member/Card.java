package com.bi.barfdog.domain.member;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Builder
@Entity
public class Card {

    @Id
    @GeneratedValue
    @Column(name = "card_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String customerUid;

    private String cardName;

    private String cardNumber;

}
