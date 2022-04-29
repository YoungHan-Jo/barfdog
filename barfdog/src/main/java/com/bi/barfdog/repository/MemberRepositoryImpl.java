package com.bi.barfdog.repository;

import com.bi.barfdog.api.couponDto.AREA;
import com.bi.barfdog.api.couponDto.GroupPublishRequestDto;
import com.bi.barfdog.api.memberDto.MemberConditionPublishCoupon;
import com.bi.barfdog.api.memberDto.MemberPublishCouponResponseDto;
import com.bi.barfdog.config.BarfCity;
import com.bi.barfdog.domain.member.Grade;
import com.bi.barfdog.domain.member.Member;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private BooleanExpression birthBetween(String birthYearFrom, String birthYearTo) {
        String from = birthYearFrom + "01" + "01";
        String to = birthYearTo + "12" + "31";
        return member.birthday.between(from,to);
    }

    private BooleanExpression areaEq(AREA area) {
        String[] metro = {BarfCity.GYEONGGI, BarfCity.INCHEON, BarfCity.SEOUL};
        if (area == AREA.METRO) {
            return member.address.city.in(metro);
        } else if (area == AREA.NON_METRO){
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

    private BooleanExpression nameEq(String name) {
        return hasText(name) ? member.name.eq(name) : null;
    }

    private BooleanExpression emailEq(String email) {
        return hasText(email) ? member.email.eq(email) : null;
    }
}
