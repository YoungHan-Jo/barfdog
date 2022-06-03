package com.bi.barfdog.api.resource;

import com.bi.barfdog.api.rewardDto.QueryRewardsDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

public class RewardDtoResource extends EntityModel<QueryRewardsDto> {
    public RewardDtoResource(QueryRewardsDto content, Link... links) {
        super(content, Arrays.asList(links));
    }
}
