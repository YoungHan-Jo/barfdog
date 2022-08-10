package com.bi.barfdog.directsend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RewardAlimDto {

    private String name;
    private String phone;
    private String dogName;
    private String rewardName;
    private int amount;

}
