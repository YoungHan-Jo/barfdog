package com.bi.barfdog.repository.reward;

import com.bi.barfdog.api.memberDto.QueryMembersCond;
import com.bi.barfdog.api.rewardDto.QueryAdminRewardsDto;
import com.bi.barfdog.api.rewardDto.QueryRewardsDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.reward.RewardType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.bi.barfdog.domain.member.QMember.*;
import static com.bi.barfdog.domain.reward.QReward.*;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Repository
public class RewardRepositoryImpl implements RewardRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<QueryAdminRewardsDto> findAdminRewardsDtoByCond(Pageable pageable, QueryMembersCond cond) {

        List<QueryAdminRewardsDto> result = queryFactory
                .select(Projections.constructor(QueryAdminRewardsDto.class,
                        reward.id,
                        reward.createdDate,
                        reward.name,
                        reward.tradeReward,
                        member.name,
                        member.email
                ))
                .from(reward)
                .join(reward.member, member)
                .where(
                        nameContains(cond.getName()),
                        emailContains(cond.getEmail()),
                        createdDateBetween(cond)
                )
                .orderBy(reward.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(reward.count())
                .from(reward)
                .join(reward.member, member)
                .where(
                        nameContains(cond.getName()),
                        emailContains(cond.getEmail()),
                        createdDateBetween(cond)
                )
                .fetchOne();

        return new PageImpl<>(result,pageable,totalCount);
    }

    @Override
    public Page<QueryRewardsDto> findRewardsDto(Member member, Pageable pageable) {

        List<QueryRewardsDto> result = queryFactory
                .select(Projections.constructor(QueryRewardsDto.class,
                        reward.createdDate,
                        reward.name,
                        reward.rewardStatus,
                        reward.tradeReward
                ))
                .from(reward)
                .where(reward.member.eq(member))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(reward.createdDate.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(reward.count())
                .from(reward)
                .where(reward.member.eq(member))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public Page<QueryRewardsDto> findRewardsDtoInvite(Member member, Pageable pageable) {

        List<QueryRewardsDto> result = queryFactory
                .select(Projections.constructor(QueryRewardsDto.class,
                        reward.createdDate,
                        reward.name,
                        reward.rewardStatus,
                        reward.tradeReward
                ))
                .from(reward)
                .where(reward.member.eq(member).and(rewardTypeEqInvite()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(reward.createdDate.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(reward.count())
                .from(reward)
                .where(reward.member.eq(member).and(rewardTypeEqInvite()))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public Long findInviteCount(Member member) {
        return queryFactory
                .select(reward.count())
                .from(reward)
                .where(reward.member.eq(member).and(rewardTypeEqInvite()))
                .fetchOne()
                ;
    }

    @Override
    public int findTotalRewardInvite(Member member) {
        return queryFactory
                .select(new CaseBuilder()
                        .when(reward.tradeReward.sum().isNull()).then(0)
                        .otherwise(reward.tradeReward.sum())
                )
                .from(reward)
                .where(reward.member.eq(member).and(rewardTypeEqInvite()))
                .fetchOne()
                ;
    }

    private BooleanExpression rewardTypeEqInvite() {
        return reward.rewardType.eq(RewardType.INVITE);
    }


    private BooleanExpression createdDateBetween(QueryMembersCond cond) {
        LocalDateTime from = cond.getFrom().atTime(0, 0, 0);
        LocalDateTime to = cond.getTo().atTime(23, 59, 59);

        return member.createdDate.between(from, to);
    }

    private BooleanExpression nameContains(String name) {
        return hasText(name) ? member.name.contains(name) : null;
    }

    private BooleanExpression emailContains(String email) {
        return hasText(email) ? member.email.contains(email) : null;
    }
}
