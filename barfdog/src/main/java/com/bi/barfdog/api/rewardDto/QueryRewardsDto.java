package com.bi.barfdog.api.rewardDto;

import com.bi.barfdog.domain.reward.RewardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryRewardsDto {

    private LocalDateTime createdTime;

    private String name;

    private RewardStatus rewardStatus; // [SAVED, USED]

    private int tradeReward;

}

