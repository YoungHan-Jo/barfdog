package com.bi.barfdog.repository;

import com.bi.barfdog.api.memberDto.QueryMembersCond;
import com.bi.barfdog.api.rewardDto.QueryAdminRewardsDto;
import com.bi.barfdog.api.rewardDto.QueryRewardsDto;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RewardRepositoryCustom {
    Page<QueryAdminRewardsDto> findAdminRewardsDtoByCond(Pageable pageable, QueryMembersCond cond);

    Page<QueryRewardsDto> findRewardsDto(Member member, Pageable pageable);

    Page<QueryRewardsDto> findRewardsDtoInvite(Member member, Pageable pageable);

    Long findInviteCount(Member member);

    int findTotalRewardInvite(Member member);
}
