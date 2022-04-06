package com.bi.barfdog.domain.rewardPoint;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class RewardPoint extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "reward_point_id")
    private Long id;
}
