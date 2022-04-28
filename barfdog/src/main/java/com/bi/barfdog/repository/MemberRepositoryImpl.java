package com.bi.barfdog.repository;

import com.bi.barfdog.api.memberDto.MemberConditionPublishCoupon;
import com.bi.barfdog.api.memberDto.MemberPublishCouponResponseDto;
import com.bi.barfdog.domain.dog.QDog;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.member.QMember;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.bi.barfdog.domain.dog.QDog.*;
import static com.bi.barfdog.domain.member.QMember.*;
import static com.querydsl.jpa.JPAExpressions.*;
import static org.springframework.util.StringUtils.*;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberPublishCouponResponseDto> searchMemberDtosInPublication(MemberConditionPublishCoupon condition) {

        List<MemberPublishCouponResponseDto> result = queryFactory
                .select(Projections.constructor(MemberPublishCouponResponseDto.class,
                                member.id,
                                member.grade,
                                member.name,
                                member.email,
                                member.phoneNumber,
                                select(dog.name)
                                        .from(dog)
                                        .where(dog.member.id.eq(member.id).and(dog.representative.eq(true))),
                                member.accumulatedAmount,
                                new CaseBuilder()
                                        .when(member.roles.contains("SUBSCRIBER")).then(true)
                                        .otherwise(false)
                        )
                )
                .from(member)
                .where(
                        nameEq(condition.getName()),
                        emailEq(condition.getEmail())
                )
                .fetch();

        return result;
    }

    @Override
    public List<Member> findByIdList(List<Long> memberIdList) {
        return queryFactory
                .selectFrom(member)
                .where(member.id.in(memberIdList))
                .fetch();
    }

    private BooleanExpression nameEq(String name) {
        return hasText(name) ? member.name.eq(name) : null;
    }

    private BooleanExpression emailEq(String email) {
        return hasText(email) ? member.email.eq(email) : null;
    }
}
