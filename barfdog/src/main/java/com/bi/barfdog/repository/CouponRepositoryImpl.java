package com.bi.barfdog.repository;

import com.bi.barfdog.api.couponDto.CouponListResponseDto;
import com.bi.barfdog.api.couponDto.PublicationCouponDto;
import com.bi.barfdog.domain.coupon.*;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
                        coupon.amount,
                        coupon.lastExpiredDate
                ))
                .from(coupon)
                .where(isDirectCoupon(keyword))
                .orderBy(coupon.id.desc())
                .fetch();

        return result;
    }

    @Override
    public List<CouponListResponseDto> findAutoCouponsByKeyword(String keyword) {
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
                        coupon.amount,
                        coupon.lastExpiredDate
                ))
                .from(coupon)
                .where(isAutoCoupon(keyword))
                .orderBy(coupon.id.desc())
                .fetch();

        return result;
    }

    @Override
    public List<PublicationCouponDto> findPublicationCouponDtosByCouponType(CouponType couponType) {

        List<PublicationCouponDto> result = queryFactory
                .select(Projections.constructor(PublicationCouponDto.class,
                        coupon.id,
                        coupon.name,
                        coupon.discountDegree.stringValue().concat(
                                coupon.discountType
                                        .when(DiscountType.FLAT_RATE).then("원")
                                        .otherwise("%")
                        )
                ))
                .from(coupon)
                .where(statusEqActive().and(couponTypeEq(couponType)))
                .orderBy(coupon.id.desc())
                .fetch();

        return result;
    }

    private BooleanExpression couponTypeEq(CouponType couponType) {
        return coupon.couponType.eq(couponType);
    }


    private BooleanExpression isDirectCoupon(String keyword) {
        return statusEqActive().and(couponTypeNeAutoPublished().and(nameLike(keyword)));
    }

    private BooleanExpression isAutoCoupon(String keyword) {
        return statusEqActive().and(couponTypeEqAutoPublished().and(nameLike(keyword)));
    }

    private BooleanExpression couponTypeEqAutoPublished() {
        return coupon.couponType.eq(CouponType.AUTO_PUBLISHED);
    }

    private BooleanExpression couponTypeNeAutoPublished() {
        return coupon.couponType.ne(CouponType.AUTO_PUBLISHED);
    }

    private BooleanExpression statusEqActive() {
        return coupon.couponStatus.eq(CouponStatus.ACTIVE);
    }

    private BooleanExpression nameLike(String keyword) {
        return keyword != null && keyword.length() > 0 ? coupon.name.contains(keyword) : null;
    }
}
