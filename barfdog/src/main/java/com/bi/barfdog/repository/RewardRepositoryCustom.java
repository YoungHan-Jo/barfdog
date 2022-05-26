package com.bi.barfdog.repository;

import com.bi.barfdog.api.memberDto.QueryMembersCond;
import com.bi.barfdog.api.rewardDto.QueryAdminRewardsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RewardRepositoryCustom {
    Page<QueryAdminRewardsDto> findAdminRewardsDtoByCond(Pageable pageable, QueryMembersCond cond);
}
