package com.bi.barfdog.repository.memberCoupon;

import com.bi.barfdog.api.orderDto.OrderSheetSubsCouponDto;
import com.bi.barfdog.domain.coupon.CouponStatus;
import com.bi.barfdog.domain.coupon.CouponTarget;
import com.bi.barfdog.domain.member.Member;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.coupon.QCoupon.coupon;
import static com.bi.barfdog.domain.memberCoupon.QMemberCoupon.memberCoupon;

@RequiredArgsConstructor
@Repository
public class MemberCouponRepositoryImpl implements MemberCouponRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OrderSheetSubsCouponDto> findSubscribeCouponDtos(Member member) {

        return queryFactory
                .select(Projections.constructor(OrderSheetSubsCouponDto.class,
                        memberCoupon.id,
                        coupon.name,
                        coupon.discountType,
                        coupon.discountDegree,
                        coupon.availableMaxDiscount,
                        coupon.availableMinPrice,
                        memberCoupon.remaining,
                        memberCoupon.expiredDate
                ))
                .from(memberCoupon)
                .join(memberCoupon.coupon, coupon)
                .where(memberCoupon.member.eq(member)
                        .and(validSubscribeCoupon())
                )
                .fetch();
    }

    private BooleanExpression validSubscribeCoupon() {
        return validCoupon().and(couponTargetInSubscribeAndAll());
    }

    private BooleanExpression validGeneralCoupon() {
        return validCoupon().and(couponTargetInGeneralAndAll());
    }

    private BooleanExpression couponTargetInSubscribeAndAll() {
        return coupon.couponTarget.in(CouponTarget.SUBSCRIBE, CouponTarget.ALL);
    }

    private BooleanExpression couponTargetInGeneralAndAll() {
        return coupon.couponTarget.in(CouponTarget.GENERAL, CouponTarget.ALL);
    }

    private BooleanExpression validCoupon() {
        return memberCouponEqActive()
                .and(remainingGt0()
                        .and(couponEqActive()));
    }
    private BooleanExpression couponEqActive() {
        return coupon.couponStatus.eq(CouponStatus.ACTIVE);
    }
    private BooleanExpression remainingGt0() {
        return memberCoupon.remaining.gt(0);
    }
    private BooleanExpression memberCouponEqActive() {
        return memberCoupon.memberCouponStatus.eq(CouponStatus.ACTIVE);
    }
}
