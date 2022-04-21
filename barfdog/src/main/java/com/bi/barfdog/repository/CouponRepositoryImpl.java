package com.bi.barfdog.repository;

import com.bi.barfdog.api.couponDto.CouponListResponseDto;
import com.bi.barfdog.domain.coupon.Coupon;
import com.bi.barfdog.domain.coupon.CouponStatus;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.coupon.QCoupon;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.coupon.QCoupon.*;

@RequiredArgsConstructor
@Repository
public class CouponRepositoryImpl implements CouponRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<CouponListResponseDto> findRedirectCouponsByKeyword(String keyword) {

        List<CouponListResponseDto> result = queryFactory
                .select(Projections.constructor(CouponListResponseDto.class,
                        coupon.id,
                        coupon.name,
                        coupon.code,
                        coupon.description,
                        coupon.discountDegree.stringValue().concat(
                                coupon.discountType
                                        .when(DiscountType.FLAT_RATE).then("원")
                                        .otherwise("%")
                        ),
                        coupon.couponTarget,
                        coupon.amount
                ))
                .from(coupon)
                .where(nameLike(keyword).and(statusEqActive()))
                .fetch();

        return result;
    }

    private BooleanExpression statusEqActive() {
        return coupon.status.eq(CouponStatus.ACTIVE);
    }

    private BooleanExpression nameLike(String keyword) {
        return keyword != null && keyword.length() > 0 ? coupon.name.contains(keyword) : null;
    }
}
