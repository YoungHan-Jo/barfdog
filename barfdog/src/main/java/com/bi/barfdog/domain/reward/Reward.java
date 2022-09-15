package com.bi.barfdog.domain.reward;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.member.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Reward extends BaseTimeEntity { // 적립금 내역

    @Id @GeneratedValue
    @Column(name = "reward_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String name; // 적립금 이름

    @Enumerated(EnumType.STRING)
    private RewardType rewardType; // [RECOMMEND, INVITE, REVIEW, EVENT, ORDER, SUBSCRIBE, ADMIN]

    @Enumerated(EnumType.STRING)
    private RewardStatus rewardStatus; // [SAVED, USED] , 각 적립/사용

    private int tradeReward; // 적립금 수량



}
