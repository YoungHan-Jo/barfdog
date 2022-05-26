package com.bi.barfdog.repository;

import com.bi.barfdog.api.memberDto.QueryMembersCond;
import com.bi.barfdog.api.rewardDto.QueryAdminRewardsDto;
import com.bi.barfdog.domain.member.QMember;
import com.bi.barfdog.domain.reward.QReward;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
                        nameEq(cond.getName()),
                        emailEq(cond.getEmail()),
                        createdDateBetween(cond)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(reward.createdDate.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        nameEq(cond.getName()),
                        emailEq(cond.getEmail()),
                        createdDateBetween(cond)
                )
                .fetchOne();

        return new PageImpl<>(result,pageable,totalCount);
    }




    private BooleanExpression createdDateBetween(QueryMembersCond cond) {
        LocalDateTime from = cond.getFrom().atTime(0, 0, 0);
        LocalDateTime to = cond.getTo().atTime(23, 59, 59);

        return member.createdDate.between(from, to);
    }

    private BooleanExpression nameEq(String name) {
        return hasText(name) ? member.name.eq(name) : null;
    }

    private BooleanExpression emailEq(String email) {
        return hasText(email) ? member.email.eq(email) : null;
    }
}
