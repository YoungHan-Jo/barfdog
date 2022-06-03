package com.bi.barfdog.api.rewardDto;

import com.bi.barfdog.api.resource.RewardDtoResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.PagedModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryRewardsPageDto {

    private int reward;

    private PagedModel<RewardDtoResource> pagedModel;

}
