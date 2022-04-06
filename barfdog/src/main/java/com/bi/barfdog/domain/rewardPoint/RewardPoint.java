package com.bi.barfdog.domain.rewardPoint;

import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.member.Member;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class RewardPoint extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "reward_point_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private RewardPointStatus rewardPointStatus; // [SAVED, USED]

    private RewardPointType type;
}
