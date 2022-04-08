package com.bi.barfdog.domain.reward;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reward extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "reward_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private RewardType rewardType; // [EVENT, ORDER, SUBSCRIBE]

    @Enumerated(EnumType.STRING)
    private RewardStatus rewardStatus; // [SAVED, USED]

    private int tradeReward;

    private String content;


}
