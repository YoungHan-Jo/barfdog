package com.bi.barfdog.repository.member;

import com.bi.barfdog.api.couponDto.Area;
import com.bi.barfdog.api.couponDto.GroupPublishRequestDto;
import com.bi.barfdog.api.memberDto.*;
import com.bi.barfdog.api.rewardDto.PublishToGroupDto;
import com.bi.barfdog.config.finalVariable.BarfCity;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
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
import java.util.Optional;

import static com.bi.barfdog.domain.dog.QDog.*;
import static com.bi.barfdog.domain.member.QMember.*;
import static com.querydsl.jpa.JPAExpressions.*;
import static org.springframework.util.StringUtils.*;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberPublishResponseDto> searchMemberDtosInPublication(MemberConditionToPublish condition) {

        List<MemberPublishResponseDto> result = queryFactory
                .select(Projections.constructor(MemberPublishResponseDto.class,
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
                        nameContains(condition.getName()),
                        emailContains(condition.getEmail())
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

    @Override
    public List<Member> findMembersByGroupCouponCond(GroupPublishRequestDto requestDto) {
        return queryFactory
                .selectFrom(member)
                .where(
                        subscribeEq(requestDto.isSubscribe()),
                        longUnconnectedEq(requestDto.isLongUnconnected()),
                        gradeIn(requestDto.getGradeList()),
                        areaEq(requestDto.getArea()),
                        birthBetween(requestDto.getBirthYearFrom(),requestDto.getBirthYearTo())
                )
                .fetch();
    }

    @Override
    public List<Member> findByGrades(List<Grade> gradeList) {
        return queryFactory
                .selectFrom(member)
                .where(member.grade.in(gradeList))
                .fetch();
    }

    @Override
    public List<Long> findMemberIdList(PublishToGroupDto requestDto) {
        return queryFactory
                .select(member.id)
                .from(member)
                .where(
                        subscribeEq(requestDto.isSubscribe()),
                        longUnconnectedEq(requestDto.isLongUnconnected()),
                        gradeIn(requestDto.getGradeList()),
                        areaEq(requestDto.getArea()),
                        birthBetween(requestDto.getBirthYearFrom(),requestDto.getBirthYearTo())
                )
                .fetch();
    }

    private BooleanExpression subscribeEq(PublishToGroupDto requestDto) {
        return member.isSubscribe.eq(requestDto.isSubscribe());
    }

    @Override
    public Page<QueryMembersDto> findDtosByCond(Pageable pageable, QueryMembersCond cond) {
        List<QueryMembersDto> result = queryFactory
                .select(Projections.constructor(QueryMembersDto.class,
                        member.id,
                        member.grade,
                        member.name,
                        member.email,
                        member.phoneNumber,
                        dog.name,
                        member.accumulatedAmount,
                        member.isSubscribe,
                        new CaseBuilder()
                                .when(member.lastLoginDate.before(LocalDateTime.now().minusYears(1L))).then(true)
                                .otherwise(false)
                ))
                .from(dog)
                .rightJoin(dog.member, member)
                .where(
                        nameContains(cond.getName()),
                        emailContains(cond.getEmail()),
                        createdDateBetween(cond)
                )
                .groupBy(member.id, dog.name)
                .having(dog.representative.eq(true).or(dog.representative.isNull()))
                .orderBy(member.email.length().asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        nameContains(cond.getName()),
                        emailContains(cond.getEmail()),
                        createdDateBetween(cond)
                )
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    @Override
    public Optional<QueryMemberDto> findMemberDto(Long id) {

        QueryMemberDto result = queryFactory
                .select(Projections.constructor(QueryMemberDto.class,
                        member.id,
                        member.name,
                        member.email,
                        member.address,
                        member.phoneNumber,
                        member.birthday,
                        member.accumulatedAmount,
                        member.grade,
                        member.isSubscribe,
                        member.accumulatedSubscribe,
                        member.lastLoginDate,
                        new CaseBuilder()
                                .when(member.lastLoginDate.before(LocalDateTime.now().minusYears(1L))).then(true)
                                .otherwise(false),
                        member.isWithdrawal
                ))
                .from(member)
                .where(member.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public int findRewardById(Long id) {
        return queryFactory
                .select(member.reward)
                .from(member)
                .where(member.id.eq(id))
                .fetchOne()
                ;
    }

    @Override
    public Long findCountByMyCode(String myRecommendationCode) {
        return queryFactory
                .select(member.count())
                .from(member)
                .where(member.recommendCode.eq(myRecommendationCode))
                .fetchOne()
                ;
    }

    @Override
    public QuerySnsDto findProviderByMember(Member currentMember) {
        return queryFactory
                .select(Projections.constructor(QuerySnsDto.class,
                        member.provider
                ))
                .from(member)
                .where(member.eq(currentMember))
                .fetchOne()
                ;
    }

    private BooleanExpression createdDateBetween(QueryMembersCond cond) {
        LocalDateTime from = cond.getFrom().atTime(0, 0, 0);
        LocalDateTime to = cond.getTo().atTime(23, 59, 59);

        return member.createdDate.between(from, to);
    }


    private BooleanExpression birthBetween(String birthYearFrom, String birthYearTo) {
        String from = birthYearFrom + "01" + "01";
        String to = birthYearTo + "12" + "31";
        return member.birthday.between(from,to);
    }

    private BooleanExpression areaEq(Area area) {
        String[] metro = {BarfCity.GYEONGGI, BarfCity.INCHEON, BarfCity.SEOUL};
        if (area == Area.METRO) {
            return member.address.city.in(metro);
        } else if (area == Area.NON_METRO){
            return member.address.city.in(metro).not();
        }else {
            return null;
        }
    }

    private BooleanExpression gradeIn(List<Grade> gradeList) {
        return member.grade.in(gradeList);
    }


    private BooleanExpression longUnconnectedEq(boolean longUnconnected) {
        return longUnconnected ? member.lastLoginDate.loe(LocalDateTime.now().minusYears(1L)) : null;
    }

    private BooleanExpression subscribeEq(boolean subscribe) {
        return subscribe ? member.roles.contains("SUBSCRIBE"): member.roles.contains("SUBSCRIBE").not();
    }

    private BooleanExpression nameContains(String name) {
        return hasText(name) ? member.name.contains(name) : null;
    }

    private BooleanExpression emailContains(String email) {
        return hasText(email) ? member.email.contains(email) : null;
    }
}
