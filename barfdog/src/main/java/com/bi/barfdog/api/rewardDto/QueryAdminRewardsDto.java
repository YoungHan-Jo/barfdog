package com.bi.barfdog.api.rewardDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAdminRewardsDto {

    private Long id;

    private LocalDateTime createdDate;

    private String name;

    private int amount;

    private String memberName;

    private String email;

}
